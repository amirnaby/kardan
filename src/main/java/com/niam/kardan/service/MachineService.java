package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.common.utils.PaginationUtils;
import com.niam.kardan.model.Machine;
import com.niam.kardan.model.basedata.MachineStatus;
import com.niam.kardan.model.basedata.MachineType;
import com.niam.kardan.repository.MachineRepository;
import jakarta.persistence.EntityExistsException;
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
public class MachineService {
    private final MachineRepository machineRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
    private final PaginationUtils paginationUtils;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private MachineService self;

    @Transactional("transactionManager")
    @CacheEvict(value = {"machines", "machine"}, allEntries = true)
    public Machine create(Machine machine) {
        machine.setMachineType(baseDataServiceFactory
                .create(MachineType.class).getByCode(machine.getMachineType().getCode()));
        machine.setMachineStatus(baseDataServiceFactory
                .create(MachineStatus.class).getByCode(machine.getMachineStatus().getCode()));
        return machineRepository.save(machine);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"machines", "machine"}, allEntries = true)
    public Machine update(Long id, Machine updated) {
        Machine existing = self.getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        existing.setMachineType(baseDataServiceFactory
                .create(MachineType.class).getByCode(updated.getMachineType().getCode()));
        existing.setMachineStatus(baseDataServiceFactory
                .create(MachineStatus.class).getByCode(updated.getMachineStatus().getCode()));
        return machineRepository.save(existing);
    }

    @Cacheable(value = "machine", key = "#id")
    public Machine getById(Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                        ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                        messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(),
                                "Machine")));
    }

    @Cacheable(value = "machines")
    public Page<Machine> getAll(Map<String, Object> requestParams) {
        PageRequest pageRequest = paginationUtils.pageHandler(requestParams);
        Specification<Machine> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestParams.get("code") != null)
                predicates.add(criteriaBuilder.equal(root.get("code"), requestParams.remove("code")));
            if (requestParams.get("location") != null)
                predicates.add(criteriaBuilder.equal(root.get("location"), requestParams.remove("location")));
            if (requestParams.get("machineType") != null)
                predicates.add(criteriaBuilder.equal(root.get("machineType").get("code"), requestParams.remove("machineType")));
            if (requestParams.get("machineStatus") != null)
                predicates.add(criteriaBuilder.equal(root.get("machineStatus").get("code"), requestParams.remove("machineStatus")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return machineRepository.findAll(specification, pageRequest);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"machines", "machine"}, allEntries = true)
    public void delete(Long id) {
        Machine machine = self.getById(id);
        try {
            machineRepository.delete(machine);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException(
                    messageUtil.getMessage(ResultResponseStatus.ENTITY_HAS_DEPENDENCIES.getDescription(), "Machine"));
        }
    }
}