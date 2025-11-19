package com.niam.kardan.service;

import com.niam.common.exception.EntityExistsException;
import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.Part;
import com.niam.kardan.model.basedata.PartStatus;
import com.niam.kardan.repository.PartRepository;
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
public class PartService {
    private final PartRepository partRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private PartService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"parts", "part"}, allEntries = true)
    public Part create(Part part) {
        part.setStatus(baseDataServiceFactory.create(PartStatus.class).getByCode(part.getStatus().getCode()));
        return partRepository.save(part);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"parts", "part"}, allEntries = true)
    public Part update(Long id, Part updated) {
        Part existing = self.getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        existing.setStatus(baseDataServiceFactory.create(PartStatus.class).getByCode(updated.getStatus().getCode()));
        return partRepository.save(existing);
    }

    @Cacheable(value = "part", key = "#id")
    public Part getById(Long id) {
        return partRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "Part")));
    }

    @Cacheable(value = "parts")
    public List<Part> getAll() {
        return partRepository.findAll();
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"parts", "part"}, allEntries = true)
    public void delete(Long id) {
        Part part = self.getById(id);
        try {
            partRepository.delete(part);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException(messageUtil.getMessage(ResultResponseStatus.ENTITY_HAS_DEPENDENCIES.getDescription(), "Part"));
        }
    }
}