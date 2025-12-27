package com.niam.kardan.service;

import com.niam.common.exception.EntityExistsException;
import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.common.utils.PaginationUtils;
import com.niam.kardan.model.Shift;
import com.niam.kardan.model.basedata.ShiftStatus;
import com.niam.kardan.repository.OperatorShiftRepository;
import com.niam.kardan.repository.ShiftRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;
    private final OperatorShiftRepository operatorShiftRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
    private final PaginationUtils paginationUtils;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private ShiftService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"shifts", "shift"}, allEntries = true)
    public Shift create(Shift shift) {
        shift.setStatus(baseDataServiceFactory.create(ShiftStatus.class).getByCode(shift.getStatus().getCode()));
        return shiftRepository.save(shift);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"shifts", "shift"}, allEntries = true)
    public Shift update(Long id, Shift updated) {
        Shift existing = self.getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        existing.setStatus(baseDataServiceFactory.create(ShiftStatus.class).getByCode(updated.getStatus().getCode()));
        return shiftRepository.save(existing);
    }

    @Cacheable(value = "shift", key = "#id")
    public Shift getById(Long id) {
        return shiftRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "Shift")));
    }

    @Cacheable(value = "shifts")
    public Page<Shift> getAll(Map<String, Object> requestParams) {
        PageRequest pageRequest = paginationUtils.pageHandler(requestParams);
        Specification<Shift> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestParams.get("name") != null) predicates.add(criteriaBuilder.equal(root.get("name"), requestParams.remove("name")));
            if (requestParams.get("statusId") != null) predicates.add(criteriaBuilder.equal(root.get("statusId").get("id"), requestParams.remove("statusId")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return shiftRepository.findAll(specification, pageRequest);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"shifts", "shift"}, allEntries = true)
    public void delete(Long id) {
        Shift shift = self.getById(id);
        boolean hasActiveAssignments = operatorShiftRepository.existsByShiftIdAndUnassignedAtIsNull(shift.getId());
        if (hasActiveAssignments) {
            throw new EntityExistsException(
                    messageUtil.getMessage(ResultResponseStatus.ENTITY_HAS_DEPENDENCIES.getDescription(), "Shift"));
        }
        try {
            shiftRepository.delete(shift);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException(
                    messageUtil.getMessage(ResultResponseStatus.ENTITY_HAS_DEPENDENCIES.getDescription(), "Shift"));
        }
    }
}