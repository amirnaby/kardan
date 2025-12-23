package com.niam.kardan.service;

import com.niam.common.exception.EntityExistsException;
import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.StopReason;
import com.niam.kardan.model.basedata.StopReasonCategory;
import com.niam.kardan.repository.StopReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StopReasonService {
    private final StopReasonRepository stopReasonRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
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
    public Page<StopReason> getAll(PageRequest pageRequest) {
        return stopReasonRepository.findAll(pageRequest);
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