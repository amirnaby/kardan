package com.niam.kardan.service;

import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.OperationExecution;
import com.niam.kardan.model.OperationStop;
import com.niam.kardan.model.StopReason;
import com.niam.kardan.model.basedata.BaseData;
import com.niam.kardan.model.basedata.ExecutionStatus;
import com.niam.kardan.model.basedata.enums.EXECUTION_STATUS;
import com.niam.kardan.repository.OperationExecutionRepository;
import com.niam.kardan.repository.OperationStopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OperationStopProcessTest {
    @Mock
    OperationExecutionRepository executionRepository;
    @Mock
    OperationStopRepository stopRepository;
    @Mock
    StopReasonService stopReasonService;
    @InjectMocks
    OperationExecutionService operationExecutionService;
    @Mock
    private GenericBaseDataServiceFactory baseDataServiceFactory;
    @Mock
    private BaseDataServiceProxy<ExecutionStatus> executionStatusService;
    @Mock
    private MessageUtil messageUtil;
    private OperationExecution execution;
    private StopReason stopReason;
    private ExecutionStatus stoppedStatus;

    @BeforeEach
    void setUp() {
        execution = new OperationExecution();
        execution.setId(55L);
        execution.setStartTime(LocalDateTime.now());

        stopReason = new StopReason();
        stopReason.setId(2L);
        stopReason.setName("Break");

        stoppedStatus = BaseData.ofCode(ExecutionStatus.class, EXECUTION_STATUS.STOPPED.name());

        when(baseDataServiceFactory.create(ExecutionStatus.class)).thenReturn(executionStatusService);
        when(executionStatusService.getByCode(EXECUTION_STATUS.STOPPED.name())).thenReturn(stoppedStatus);
    }

    @Test
    void stopExecution_createsOperationStop_and_updatesExecutionStatus() {
        execution.setExecutionStatus(new ExecutionStatus());
        execution.getExecutionStatus().setCode(EXECUTION_STATUS.STARTED.name());

        when(executionRepository.findById(55L)).thenReturn(Optional.of(execution));
        when(stopReasonService.getById(2L)).thenReturn(stopReason);
        when(executionStatusService.getByCode(EXECUTION_STATUS.STOPPED.name())).thenReturn(stoppedStatus);

        when(stopRepository.save(any(OperationStop.class))).thenAnswer(i -> {
            OperationStop s = (OperationStop) i.getArguments()[0];
            s.setId(99L);
            return s;
        });
        when(executionRepository.save(any(OperationExecution.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- Execute stop ---
        OperationStop stop = operationExecutionService.stopExecution(55L, 2L, "emergency");

        assertThat(stop).isNotNull();
        assertThat(stop.getId()).isEqualTo(99L);
        assertThat(execution.getExecutionStatus().getCode()).isEqualTo(EXECUTION_STATUS.STOPPED.name());
        assertThat(execution.getStopTime()).isNotNull();

        verify(stopRepository).save(any(OperationStop.class));
        verify(executionRepository).save(any(OperationExecution.class));
    }

    @Test
    void resumeExecution_afterStop_setsResumedStatusAndStartTime() {
        // simulate execution currently stopped
        execution.setExecutionStatus(stoppedStatus);
        execution.setStopTime(LocalDateTime.now().minusMinutes(5));

        when(executionRepository.findById(55L)).thenReturn(Optional.of(execution));
        when(executionRepository.save(any(OperationExecution.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- Execute resume ---
        OperationExecution resumed = operationExecutionService.resumeAfterStop(55L);

        assertThat(resumed.getExecutionStatus().getCode()).isEqualTo(EXECUTION_STATUS.RUNNING.name());
        assertThat(resumed.getStartTime()).isNotNull();
        assertThat(resumed.getStopTime()).isEqualTo(execution.getStopTime());

        verify(executionRepository).save(execution);
    }
}