package com.niam.kardan.service;

import com.niam.common.exception.IllegalStateException;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.*;
import com.niam.kardan.model.basedata.BaseData;
import com.niam.kardan.model.basedata.ExecutionStatus;
import com.niam.kardan.model.basedata.TaskStatus;
import com.niam.kardan.model.basedata.enums.EXECUTION_STATUS;
import com.niam.kardan.model.basedata.enums.TASK_STATUS;
import com.niam.kardan.repository.OperationExecutionRepository;
import com.niam.kardan.repository.OperatorMachineRepository;
import com.niam.kardan.repository.PartOperationTaskRepository;
import com.niam.usermanagement.model.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationExecutionProcessTest {
    @Mock
    PartOperationTaskRepository partOperationTaskRepository;
    @Mock
    OperationExecutionRepository operationExecutionRepository;
    @Mock
    MachineService machineService;
    @Mock
    OperatorService operatorService;
    @Mock
    OperatorMachineRepository operatorMachineRepository;
    @InjectMocks
    OperationExecutionService operationExecutionService;
    @Mock
    private GenericBaseDataServiceFactory baseDataServiceFactory;
    @Mock
    private GenericBaseDataService<TaskStatus> taskStatusService;
    @Mock
    private GenericBaseDataService<ExecutionStatus> executionStatusService;
    @Mock
    private MessageUtil messageUtil;
    private PartOperationTask task;
    private Operator operator;
    private Machine machine;

    @BeforeEach
    void setUp() {
        task = new PartOperationTask();
        task.setId(100L);

        TaskStatus pendingStatus = BaseData.ofCode(TaskStatus.class, TASK_STATUS.PENDING.name());
        task.setTaskStatus(pendingStatus);

        machine = new Machine();
        machine.setId(200L);
        machine.setCode("M-1");
        task.setTargetMachine(machine);

        task.setPartOperation(new PartOperation());

        operator = new Operator();
        operator.setId(10L);
        User user = new User();
        user.setUsername("testuser");
        operator.setUser(user);
    }

    @Test
    void claimAndStartTask_success_then_finish() {
        TaskStatus inProgressStatus = BaseData.ofCode(TaskStatus.class, TASK_STATUS.IN_PROGRESS.name());
        TaskStatus completedTaskStatus = BaseData.ofCode(TaskStatus.class, TASK_STATUS.COMPLETED.name());

        ExecutionStatus startedStatus = BaseData.ofCode(ExecutionStatus.class, EXECUTION_STATUS.STARTED.name());
        ExecutionStatus completedExecStatus = BaseData.ofCode(ExecutionStatus.class, EXECUTION_STATUS.COMPLETED.name());

        when(baseDataServiceFactory.create(TaskStatus.class)).thenReturn(taskStatusService);
        when(baseDataServiceFactory.create(ExecutionStatus.class)).thenReturn(executionStatusService);

        when(taskStatusService.getByCode(TASK_STATUS.IN_PROGRESS.name())).thenReturn(inProgressStatus);
        when(taskStatusService.getByCode(TASK_STATUS.COMPLETED.name())).thenReturn(completedTaskStatus);
        when(executionStatusService.getByCode(EXECUTION_STATUS.STARTED.name())).thenReturn(startedStatus);
        when(executionStatusService.getByCode(EXECUTION_STATUS.COMPLETED.name())).thenReturn(completedExecStatus);

        when(partOperationTaskRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(task));
        when(operatorService.getById(10L)).thenReturn(operator);
        when(machineService.getById(200L)).thenReturn(machine);
        when(operatorMachineRepository.existsByOperatorIdAndMachineIdAndUnassignedAtIsNull(10L, 200L)).thenReturn(true);
        when(operationExecutionRepository.save(any(OperationExecution.class))).thenAnswer(i -> i.getArguments()[0]);
        when(partOperationTaskRepository.save(any(PartOperationTask.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- اجرای claim و start ---
        OperationExecution exec = operationExecutionService.claimAndStartTask(100L, 10L, 200L);

        // --- assertions بعد از start ---
        assertThat(exec).isNotNull();
        assertThat(exec.getOperator().getId()).isEqualTo(10L);
        assertThat(task.getTaskStatus().getCode()).isEqualTo(TASK_STATUS.IN_PROGRESS.name());
        verify(partOperationTaskRepository).save(task);
        verify(operationExecutionRepository).save(any(OperationExecution.class));

        when(operationExecutionRepository.findById(exec.getId())).thenReturn(Optional.of(exec));
        when(partOperationTaskRepository.save(any(PartOperationTask.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationExecutionRepository.save(any(OperationExecution.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- اجرای finish ---
        OperationExecution finished = operationExecutionService.finishExecution(exec.getId());

        // --- assertions بعد از finish ---
        assertThat(finished.getExecutionStatus().getCode()).isEqualTo(EXECUTION_STATUS.COMPLETED.name());
        assertThat(finished.getEndTime()).isNotNull();
        verify(partOperationTaskRepository, atLeastOnce()).save(any(PartOperationTask.class));
        verify(operationExecutionRepository, atLeastOnce()).save(exec);
    }

    @Test
    void claimAndStartTask_whenNotPending_throws() {
        task.setTaskStatus(new TaskStatus());
        task.getTaskStatus().setCode(TASK_STATUS.CLAIMED.name());

        when(partOperationTaskRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> operationExecutionService.claimAndStartTask(100L, 10L, 200L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void claimAndStartTask_whenOperatorNotAssigned_throws() {
        when(partOperationTaskRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(task));
        when(operatorService.getById(10L)).thenReturn(operator);
        when(machineService.getById(200L)).thenReturn(machine);
        when(operatorMachineRepository.existsByOperatorIdAndMachineIdAndUnassignedAtIsNull(10L, 200L)).thenReturn(false);

        assertThatThrownBy(() -> operationExecutionService.claimAndStartTask(100L, 10L, 200L))
                .isInstanceOf(IllegalStateException.class);
    }
}