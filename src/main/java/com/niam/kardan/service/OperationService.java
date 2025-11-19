package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.Operation;
import com.niam.kardan.repository.OperationRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private OperationService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"operations", "operation"}, allEntries = true)
    public Operation create(Operation operation) {
        return operationRepository.save(operation);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operations", "operation"}, allEntries = true)
    public Operation update(Long id, Operation updated) {
        Operation existing = self.getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return operationRepository.save(existing);
    }

    @Cacheable(value = "operation", key = "#id")
    public Operation getById(Long id) {
        return operationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "Operation")));
    }

    @Cacheable(value = "operations")
    public List<Operation> getAll() {
        return operationRepository.findAll();
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operations", "operation"}, allEntries = true)
    public void delete(Long id) {
        Operation operation = self.getById(id);
        try {
            operationRepository.delete(operation);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException(
                    messageUtil.getMessage(ResultResponseStatus.ENTITY_HAS_DEPENDENCIES.getDescription(), "Operation"));
        }
    }
}