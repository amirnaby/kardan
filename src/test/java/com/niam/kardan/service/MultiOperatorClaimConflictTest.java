package com.niam.kardan.service;

import com.niam.common.exception.IllegalStateException;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.Machine;
import com.niam.kardan.model.OperationExecution;
import com.niam.kardan.model.Operator;
import com.niam.kardan.model.PartOperationTask;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultiOperatorClaimConflictTest {
    @Mock
    private PartOperationTaskRepository partOperationTaskRepository;
    @Mock
    private OperationExecutionRepository operationExecutionRepository;
    @Mock
    private OperatorMachineRepository operatorMachineRepository;
    @Mock
    private MachineService machineService;
    @Mock
    private OperatorService operatorService;
    @Mock
    private GenericBaseDataServiceFactory baseDataServiceFactory;
    @Mock
    private GenericBaseDataService baseDataService;
    @Mock
    private MessageUtil messageUtil;
    @InjectMocks
    private OperationExecutionService operationExecutionService;

    private PartOperationTask task;
    private Operator op1, op2;
    private Machine machine;

    @BeforeEach
    void setup() {
        task = new PartOperationTask();
        task.setId(10L);
        task.setTaskStatus(BaseData.ofCode(TaskStatus.class, TASK_STATUS.PENDING.name()));

        machine = new Machine();
        machine.setId(700L);
        machine.setCode("M-700");
        task.setTargetMachine(machine);

        op1 = new Operator();
        op1.setId(1L);
        User u1 = new User();
        u1.setUsername("op1");
        op1.setUser(u1);

        op2 = new Operator();
        op2.setId(2L);
        User u2 = new User();
        u2.setUsername("op2");
        op2.setUser(u2);
    }

    @Test
    void firstOperatorWinsClaimSecondFails() {
        when(baseDataServiceFactory.create((Class<BaseData>) any())).thenReturn(baseDataService);

        when(baseDataService.getByCode(TASK_STATUS.IN_PROGRESS.name()))
                .thenReturn(BaseData.ofCode(TaskStatus.class, TASK_STATUS.IN_PROGRESS.name()));
        when(baseDataService.getByCode(EXECUTION_STATUS.RUNNING.name()))
                .thenReturn(BaseData.ofCode(ExecutionStatus.class, EXECUTION_STATUS.RUNNING.name()));

        // when findByIdForUpdate called first time, return task (PENDING)
        when(partOperationTaskRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(task));

        when(operatorService.getById(1L)).thenReturn(op1);
        when(machineService.getById(700L)).thenReturn(machine);
        when(operatorMachineRepository.existsByOperatorIdAndMachineIdAndUnassignedAtIsNull(1L, 700L))
                .thenReturn(true);

        when(partOperationTaskRepository.save(any(PartOperationTask.class))).thenAnswer(i -> i.getArgument(0));
        when(operationExecutionRepository.save(any(OperationExecution.class))).thenAnswer(i -> {
            OperationExecution e = i.getArgument(0);
            e.setId(100L);
            return e;
        });

        // first operator succeeds
        OperationExecution exec1 = operationExecution_service_call_claimStart(10L, 1L, 700L);
        assertThat(exec1).isNotNull();
        assertThat(task.getTaskStatus().getCode()).isEqualTo(TASK_STATUS.IN_PROGRESS.name());

        // second operator now sees IN_PROGRESS
        when(partOperationTaskRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> operationExecutionService.claimAndStartTask(10L, 2L, 700L))
                .isInstanceOf(IllegalStateException.class);
    }

    private OperationExecution operationExecution_service_call_claimStart(Long taskId, Long operatorId, Long machineId) {
        return operationExecutionService.claimAndStartTask(taskId, operatorId, machineId);
    }
}