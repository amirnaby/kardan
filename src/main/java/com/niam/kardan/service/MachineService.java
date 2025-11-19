package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.Machine;
import com.niam.kardan.model.basedata.MachineStatus;
import com.niam.kardan.model.basedata.MachineType;
import com.niam.kardan.repository.MachineRepository;
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
public class MachineService {
    private final MachineRepository machineRepository;
    private final GenericBaseDataServiceFactory baseDataServiceFactory;
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
    public List<Machine> getAll() {
        return machineRepository.findAll();
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