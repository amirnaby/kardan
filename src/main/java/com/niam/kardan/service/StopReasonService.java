package com.niam.kardan.service;

import com.niam.common.exception.EntityExistsException;
import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.common.utils.PaginationUtils;
import com.niam.kardan.model.StopReason;
import com.niam.kardan.model.basedata.StopReasonCategory;
import com.niam.kardan.repository.StopReasonRepository;
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
public class StopReasonService {
    private final StopReasonRepository stopReasonRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
    private final PaginationUtils paginationUtils;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private StopReasonService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"stopReasons", "stopReason"}, allEntries = true)
    public StopReason create(StopReason stopReason) {
        stopReason.setCategory(baseDataServiceFactory.create(StopReasonCategory.class).getByCode(stopReason.getCategory().getCode()));
        return stopReasonRepository.save(stopReason);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"stopReasons", "stopReason"}, allEntries = true)
    public StopReason update(Long id, StopReason updated) {
        StopReason existing = self.getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        existing.setCategory(baseDataServiceFactory.create(StopReasonCategory.class).getByCode(updated.getCategory().getCode()));
        return stopReasonRepository.save(existing);
    }

    @Cacheable(value = "stopReason", key = "#id")
    public StopReason getById(Long id) {
        return stopReasonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "StopReason")));
    }

    @Cacheable(value = "stopReasons")
    public Page<StopReason> getAll(Map<String, Object> requestParams) {
        PageRequest pageRequest = paginationUtils.pageHandler(requestParams);
        Specification<StopReason> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestParams.get("name") != null) predicates.add(criteriaBuilder.equal(root.get("name"), requestParams.remove("name")));
            if (requestParams.get("category") != null) predicates.add(criteriaBuilder.equal(root.get("category").get("code"), requestParams.remove("category")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return stopReasonRepository.findAll(specification, pageRequest);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"stopReasons", "stopReason"}, allEntries = true)
    public void delete(Long id) {
        StopReason reason = self.getById(id);
        try {
            stopReasonRepository.delete(reason);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException(
                    messageUtil.getMessage(ResultResponseStatus.ENTITY_HAS_DEPENDENCIES.getDescription(), "StopReason"));
        }
    }
}