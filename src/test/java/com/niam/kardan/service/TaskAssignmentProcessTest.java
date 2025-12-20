package com.niam.kardan.service;

import com.niam.common.exception.IllegalStateException;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.Machine;
import com.niam.kardan.model.OperationExecution;
import com.niam.kardan.model.PartOperationTask;
import com.niam.kardan.model.UserAccount;
import com.niam.kardan.model.basedata.BaseData;
import com.niam.kardan.model.basedata.ExecutionStatus;
import com.niam.kardan.model.basedata.TaskStatus;
import com.niam.kardan.model.basedata.enums.EXECUTION_STATUS;
import com.niam.kardan.model.basedata.enums.TASK_STATUS;
import com.niam.kardan.repository.OperationExecutionRepository;
import com.niam.kardan.repository.OperatorMachineRepository;
import com.niam.kardan.repository.PartOperationTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskAssignmentProcessTest {
    @Mock
    PartOperationTaskRepository partOperationTaskRepository;
    @Mock
    OperationExecutionRepository operationExecutionRepository;
    @Mock
    OperatorMachineRepository operatorMachineRepository;
    @Mock
    MachineService machineService;
    @Mock
    UserAccountService operatorService;
    @Mock
    MessageUtil messageUtil;
    @InjectMocks
    OperationExecutionService operationExecutionService;
    @Mock
    private GenericBaseDataServiceFactory baseDataServiceFactory;
    @Mock
    private BaseDataServiceProxy<TaskStatus> taskStatusService;
    @Mock
    private BaseDataServiceProxy<ExecutionStatus> executionStatusService;
    private PartOperationTask task;
    private UserAccount op1;
    private Machine machine;

    @BeforeEach
    void setup() {
        task = new PartOperationTask();
        task.setId(500L);
        task.setTaskStatus(BaseData.ofCode(TaskStatus.class, TASK_STATUS.PENDING.name()));
        machine = new Machine();
        machine.setId(700L);
        task.setTargetMachine(machine);
        op1 = new UserAccount();
        op1.setId(1L);

        when(baseDataServiceFactory.create(TaskStatus.class)).thenReturn(taskStatusService);
        when(baseDataServiceFactory.create(ExecutionStatus.class)).thenReturn(executionStatusService);
    }

    @Test
    void firstOperatorWinsClaimSecondFails() {
        when(partOperationTaskRepository.findByIdForUpdate(500L)).thenReturn(Optional.of(task));
        when(operatorService.getById(1L)).thenReturn(op1);
        when(machineService.getById(700L)).thenReturn(machine);
        when(operatorMachineRepository.existsByOperatorIdAndMachineIdAndUnassignedAtIsNull(1L, 700L)).thenReturn(true);
        when(taskStatusService.getByCode(TASK_STATUS.IN_PROGRESS.name())).thenReturn(BaseData.ofCode(TaskStatus.class, TASK_STATUS.IN_PROGRESS.name()));
        when(executionStatusService.getByCode(EXECUTION_STATUS.STARTED.name())).thenReturn(BaseData.ofCode(ExecutionStatus.class, EXECUTION_STATUS.STARTED.name()));
        when(operationExecutionRepository.save(any(OperationExecution.class))).thenAnswer(i -> i.getArguments()[0]);
        when(partOperationTaskRepository.save(any(PartOperationTask.class))).thenAnswer(i -> i.getArguments()[0]);

        OperationExecution exec1 = operationExecutionService.claimAndStartTask(500L, 1L, 700L);
        assertThat(exec1).isNotNull();

        // second operator tries
        task.setTaskStatus(BaseData.ofCode(TaskStatus.class, TASK_STATUS.IN_PROGRESS.name()));
        when(partOperationTaskRepository.findByIdForUpdate(500L)).thenReturn(Optional.of(task));
        assertThatThrownBy(() -> operationExecutionService.claimAndStartTask(500L, 2L, 700L))
                .isInstanceOf(IllegalStateException.class);
    }
}