package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.PartOperationTask;
import com.niam.kardan.model.basedata.TaskStatus;
import com.niam.kardan.model.basedata.enums.TASK_STATUS;
import com.niam.kardan.repository.PartOperationTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartOperationTaskService {
    private final PartOperationTaskRepository partOperationTaskRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
    private final MessageUtil messageUtil;

    @Transactional("transactionManager")
    public PartOperationTask create(PartOperationTask task) {
        TaskStatus pendingStatus = baseDataServiceFactory.create(TaskStatus.class).getByCode("PENDING");
        task.setTaskStatus(pendingStatus);
        return partOperationTaskRepository.save(task);
    }

    @Transactional("transactionManager")
    public PartOperationTask update(Long id, PartOperationTask updatedTask) {
        PartOperationTask existing = getById(id);
        existing.setTargetMachine(updatedTask.getTargetMachine());
        existing.setParentTask(updatedTask.getParentTask());
        existing.setPartOperation(updatedTask.getPartOperation());
        return partOperationTaskRepository.save(existing);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public PartOperationTask getById(Long id) {
        return partOperationTaskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "PartOperationTask")));
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public List<PartOperationTask> getAll() {
        return partOperationTaskRepository.findAll();
    }

    @Transactional("transactionManager")
    public void delete(Long id) {
        PartOperationTask task = getById(id);
        partOperationTaskRepository.delete(task);
    }

    @Transactional("transactionManager")
    public PartOperationTask markAsCompleted(Long id) {
        PartOperationTask task = getById(id);
        TaskStatus completedStatus = baseDataServiceFactory.create(TaskStatus.class).getByCode(TASK_STATUS.COMPLETED.name());
        task.setFinishedAt(LocalDateTime.now());
        task.setTaskStatus(completedStatus);
        return partOperationTaskRepository.save(task);
    }
}