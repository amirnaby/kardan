package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.IllegalStateException;
import com.niam.common.exception.OperationFailedException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.common.utils.PaginationUtils;
import com.niam.kardan.model.OperatorMachine;
import com.niam.kardan.repository.OperatorMachineRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OperatorMachineService {
    private final OperatorMachineRepository operatorMachineRepository;
    private final PaginationUtils paginationUtils;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private OperatorMachineService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"operatorMachines", "operatorMachine"}, allEntries = true)
    public OperatorMachine create(OperatorMachine operatorMachine) {
        boolean exists = operatorMachineRepository.existsByOperatorIdAndMachineIdAndUnassignedAtIsNull(
                operatorMachine.getOperator().getId(), operatorMachine.getMachine().getId()
        );
        if (exists) {
            throw new IllegalStateException(
                    ResultResponseStatus.DUPLICATE_ENTITY.getResponseCode(),
                    ResultResponseStatus.DUPLICATE_ENTITY.getReasonCode(),
                    messageUtil.getMessage(
                            ResultResponseStatus.DUPLICATE_ENTITY.getDescription(), "OperatorMachine"));
        }

        operatorMachine.setAssignedAt(LocalDateTime.now());
        return operatorMachineRepository.save(operatorMachine);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operatorMachines", "operatorMachine"}, allEntries = true)
    public OperatorMachine update(Long id, OperatorMachine updated) {
        OperatorMachine existing = operatorMachineRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "OperatorMachine")));

        BeanUtils.copyProperties(updated, existing, "id", "operator", "machine", "assignedAt");
        return operatorMachineRepository.save(existing);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "operatorMachine", key = "#id")
    public OperatorMachine getById(Long id) {
        return operatorMachineRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "OperatorMachine")));
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "operatorMachines")
    public Page<OperatorMachine> getAll(Map<String, Object> requestParams) {
        PageRequest pageRequest = paginationUtils.pageHandler(requestParams);
        Specification<OperatorMachine> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestParams.get("operatorId") != null) predicates.add(criteriaBuilder.equal(root.get("operatorId").get("id"), requestParams.remove("operatorId")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return operatorMachineRepository.findAll(specification, pageRequest);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operatorMachines", "operatorMachine"}, allEntries = true)
    public void unassign(Long id) {
        OperatorMachine existing = self.getById(id);
        if (existing.getUnassignedAt() != null) {
            throw new OperationFailedException(
                    ResultResponseStatus.OPERATOR_MACHINE_ALREADY_UNASSIGNED.getResponseCode(),
                    ResultResponseStatus.OPERATOR_MACHINE_ALREADY_UNASSIGNED.getReasonCode(),
                    messageUtil.getMessage(ResultResponseStatus.OPERATOR_MACHINE_ALREADY_UNASSIGNED.getDescription()));
        }
        existing.setUnassignedAt(LocalDateTime.now());
        operatorMachineRepository.save(existing);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public List<OperatorMachine> findActiveMachinesByOperator(Long operatorId) {
        return operatorMachineRepository.findByOperatorIdAndUnassignedAtIsNull(operatorId);
    }
}