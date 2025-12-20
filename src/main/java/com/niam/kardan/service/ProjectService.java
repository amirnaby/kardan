package com.niam.kardan.service;

import com.niam.common.exception.EntityExistsException;
import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.Project;
import com.niam.kardan.model.basedata.ProjectStatus;
import com.niam.kardan.repository.ProjectRepository;
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
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private ProjectService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"projects", "project"}, allEntries = true)
    public Project create(Project project) {
        project.setStatus(baseDataServiceFactory.create(ProjectStatus.class).getByCode(project.getStatus().getCode()));
        return projectRepository.save(project);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"projects", "project"}, allEntries = true)
    public Project update(Long id, Project updated) {
        Project existing = self.getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        existing.setStatus(baseDataServiceFactory.create(ProjectStatus.class).getByCode(updated.getStatus().getCode()));
        return projectRepository.save(existing);
    }

    @Cacheable(value = "project", key = "#id")
    public Project getById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "Project")));
    }

    @Cacheable(value = "projects")
    public Page<Project> getAll(PageRequest pageRequest) {
        return projectRepository.findAll(pageRequest);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"projects", "project"}, allEntries = true)
    public void delete(Long id) {
        Project project = self.getById(id);
        try {
            projectRepository.delete(project);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException(messageUtil.getMessage(ResultResponseStatus.ENTITY_HAS_DEPENDENCIES.getDescription(), "Project"));
        }
    }
}