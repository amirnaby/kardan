package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.IllegalStateException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.*;
import com.niam.kardan.model.basedata.BaseData;
import com.niam.kardan.model.basedata.ExecutionStatus;
import com.niam.kardan.model.basedata.TaskStatus;
import com.niam.kardan.model.basedata.enums.EXECUTION_STATUS;
import com.niam.kardan.model.basedata.enums.TASK_STATUS;
import com.niam.kardan.repository.OperationExecutionRepository;
import com.niam.kardan.repository.OperationStopRepository;
import com.niam.kardan.repository.OperatorMachineRepository;
import com.niam.kardan.repository.PartOperationTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperationExecutionService {
    private final OperationExecutionRepository operationExecutionRepository;
    private final PartOperationTaskRepository partOperationTaskRepository;
    private final OperationStopRepository operationStopRepository;
    private final OperatorMachineRepository operatorMachineRepository;
    private final MachineService machineService;
    private final UserAccountService operatorService;
    private final StopReasonService stopReasonService;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
    private final MessageUtil messageUtil;

    public OperationExecution getById(Long id) {
        return operationExecutionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(
                        ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "OperationExecution")));
    }

    /**
     * Claim and start a task.
     * Concurrency safe: uses PESSIMISTIC_WRITE lock on task row.
     */
    @Transactional("transactionManager")
    public OperationExecution claimAndStartTask(Long taskId, Long operatorId, Long machineId) {
        PartOperationTask task = partOperationTaskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                        ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                        messageUtil.getMessage(
                                ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "PartOperationTask")));

        // Validate task status = PENDING
        if (!TASK_STATUS.PENDING.name().equalsIgnoreCase(task.getTaskStatus().getCode())) {
            throw new IllegalStateException(
                    messageUtil.getMessage("task.not.pending", String.valueOf(taskId)));
        }

        UserAccount operator = operatorService.getById(operatorId);

        Machine machine = machineService.getById(machineId);

        // Validate machine match
        if (!task.getTargetMachine().getId().equals(machine.getId())) {
            throw new IllegalStateException(
                    messageUtil.getMessage("task.machine.mismatch", String.valueOf(taskId)));
        }

        // Check operator authorized for machine (active OperatorMachine)
        boolean operatorCanUse = operatorMachineRepository
                .existsByOperatorIdAndMachineIdAndUnassignedAtIsNull(operator.getId(), machine.getId());
        if (!operatorCanUse) {
            throw new IllegalStateException(
                    messageUtil.getMessage("operator.notAssignedToMachine",
                            operator.getUser() != null ? operator.getUser().getUsername() : "unknown"));
        }

        // Mark task as claimed and started
        task.setClaimedBy(operator);
        task.setClaimedAt(LocalDateTime.now());
        task.setStartedAt(LocalDateTime.now());
        task.setTaskStatus(baseDataServiceFactory.create(TaskStatus.class).getByCode(TASK_STATUS.IN_PROGRESS.name()));
        partOperationTaskRepository.save(task);

        // Create OperationExecution
        OperationExecution exec = new OperationExecution();
        exec.setTask(task);
        exec.setPartOperation(task.getPartOperation());
        exec.setMachine(machine);
        exec.setOperator(operator);
        exec.setStartTime(LocalDateTime.now());
        exec.setExecutionStatus(baseDataServiceFactory.create(ExecutionStatus.class).getByCode(EXECUTION_STATUS.STARTED.name()));
        return operationExecutionRepository.save(exec);
    }

    /**
     * Stop execution with a reason (creates OperationStop and marks execution stopped).
     */
    @Transactional("transactionManager")
    public OperationStop stopExecution(Long executionId, Long stopReasonId, String comment) {
        OperationExecution exec = getById(executionId);
        StopReason stopReason = stopReasonService.getById(stopReasonId);

        // Change execution status to STOPPED
        exec.setStopTime(LocalDateTime.now());
        exec.setExecutionStatus(baseDataServiceFactory.create(ExecutionStatus.class).getByCode(EXECUTION_STATUS.STOPPED.name()));
        operationExecutionRepository.save(exec);

        // Record stop record
        OperationStop stop = new OperationStop();
        stop.setOperationExecution(exec);
        stop.setStopReason(stopReason);
        stop.setComment(comment);
        stop.setStartedAt(LocalDateTime.now());
        return operationStopRepository.save(stop);
    }

    /**
     * Resume execution after stop.
     * Closes all open OperationStop records and changes status to RUNNING.
     */
    @Transactional("transactionManager")
    public OperationExecution resumeAfterStop(Long executionId) {
        OperationExecution execution = getById(executionId);

        if (!EXECUTION_STATUS.STOPPED.name().equalsIgnoreCase(execution.getExecutionStatus().getCode())) {
            throw new IllegalStateException(
                    messageUtil.getMessage("execution.must.be.stopped", String.valueOf(executionId)));
        }

        // Close all open OperationStop entries
        operationStopRepository.findAllByOperationExecutionIdAndEndedAtIsNull(executionId)
                .forEach(stop -> {
                    stop.setEndedAt(LocalDateTime.now());
                    operationStopRepository.save(stop);
                });

        // Change execution status to RUNNING
        execution.setExecutionStatus(BaseData.ofCode(ExecutionStatus.class, EXECUTION_STATUS.RUNNING.name()));
        execution.setStartTime(LocalDateTime.now());
        return operationExecutionRepository.save(execution);
    }

    /**
     * Finish execution: mark execution completed and update task status to COMPLETED.
     */
    @Transactional("transactionManager")
    public OperationExecution finishExecution(Long executionId) {
        OperationExecution exec = getById(executionId);

        if (!EXECUTION_STATUS.STARTED.name().equalsIgnoreCase(exec.getExecutionStatus().getCode())
                && !EXECUTION_STATUS.STOPPED.name().equalsIgnoreCase(exec.getExecutionStatus().getCode())) {
            throw new IllegalStateException(
                    messageUtil.getMessage("execution.notStartOrStopped", String.valueOf(executionId)));
        }

        exec.setEndTime(LocalDateTime.now());
        exec.setExecutionStatus(baseDataServiceFactory.create(ExecutionStatus.class).getByCode(EXECUTION_STATUS.COMPLETED.name()));

        // Update task status to COMPLETED
        PartOperationTask task = exec.getTask();
        task.setFinishedAt(LocalDateTime.now());
        task.setTaskStatus(baseDataServiceFactory.create(TaskStatus.class).getByCode(TASK_STATUS.COMPLETED.name()));

        partOperationTaskRepository.save(task);
        return operationExecutionRepository.save(exec);
    }

    /**
     * Reassign execution to another machine.
     */
    public OperationExecution reassignMachine(Long executionId, Long newMachineId) {
        OperationExecution exec = getById(executionId);
        Machine machine = machineService.getById(newMachineId);

        exec.setMachine(machine);
        return operationExecutionRepository.save(exec);
    }
}