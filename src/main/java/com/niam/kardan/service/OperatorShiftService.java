package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.IllegalStateException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.OperatorShift;
import com.niam.kardan.repository.OperatorShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OperatorShiftService {
    private final OperatorShiftRepository operatorShiftRepository;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    OperatorShiftService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"operatorShift", "operatorShifts"}, allEntries = true)
    public OperatorShift create(OperatorShift operatorShift) {
        boolean exists = operatorShiftRepository.existsByOperatorIdAndUnassignedAtIsNull(operatorShift.getOperator().getId());
        if (exists) {
            throw new IllegalStateException(messageUtil.getMessage(
                    ResultResponseStatus.DUPLICATE_ENTITY.getDescription(), "OperatorShift"));
        }
        operatorShift.setAssignedAt(LocalDateTime.now());
        return operatorShiftRepository.save(operatorShift);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operatorShift", "operatorShifts"}, allEntries = true)
    public OperatorShift update(Long id, OperatorShift updated) {
        OperatorShift existing = self.getById(id);
        BeanUtils.copyProperties(updated, existing, "id", "operator", "shift", "assignedAt");
        return operatorShiftRepository.save(existing);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "operatorShift", key = "#id")
    public OperatorShift getById(Long id) {
        return operatorShiftRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "OperatorShift")));
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "operatorShifts")
    public Page<OperatorShift> getAll(PageRequest pageRequest) {
        return operatorShiftRepository.findAll(pageRequest);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operatorShift", "operatorShifts"}, allEntries = true)
    public void unassign(Long id) {
        OperatorShift shift = self.getById(id);
        if (shift.getUnassignedAt() != null) {
            throw new IllegalStateException(messageUtil.getMessage(
                    "error.operatorShift.alreadyUnassigned", "OperatorShift"));
        }
        shift.setUnassignedAt(LocalDateTime.now());
        operatorShiftRepository.save(shift);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public List<OperatorShift> findActiveShiftsByOperator(Long operatorId) {
        return operatorShiftRepository.findByOperatorIdAndUnassignedAtIsNull(operatorId);
    }
}