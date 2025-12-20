package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.basedata.BaseData;
import com.niam.kardan.model.dto.BaseDataDTO;
import com.niam.kardan.service.BaseDataServiceProxy;
import com.niam.kardan.service.GenericBaseDataServiceFactory;
import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/basedata")
@RequiredArgsConstructor
public class BaseDataController {
    private final GenericBaseDataServiceFactory factory;
    private final ResponseEntityUtil responseEntityUtil;

    /**
     * Example: POST /api/basedata/ExecutionStatus
     * body: {"code":"STARTED","name":"Started","description":"Task has started"}
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{entity}")
    public ResponseEntity<BaseData> create(@PathVariable String entity, @RequestBody BaseDataDTO payload) {
        Class<? extends BaseData> type = resolveEntity(entity);
        BaseDataServiceProxy<? extends BaseData> service = factory.create(type);
        return ResponseEntity.ok(service.create(payload));
    }

    /**
     * Example: PUT /api/basedata/ExecutionStatus/1
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{entity}/{id}")
    public ResponseEntity<ServiceResponse> update(
            @PathVariable String entity,
            @PathVariable Long id,
            @RequestBody BaseDataDTO payload) {
        Class<? extends BaseData> type = resolveEntity(entity);
        BaseDataServiceProxy<? extends BaseData> service = factory.create(type);
        return responseEntityUtil.ok(service.update(id, payload));
    }

    /**
     * Example: DELETE /api/basedata/ExecutionStatus/1
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{entity}/{id}")
    public ResponseEntity<ServiceResponse> delete(@PathVariable String entity, @PathVariable Long id) {
        Class<? extends BaseData> type = resolveEntity(entity);
        BaseDataServiceProxy<? extends BaseData> service = factory.create(type);
        service.delete(id);
        return responseEntityUtil.ok(entity + " has been deleted successfully!");
    }

    /**
     * Example: GET /api/basedata/entities
     * Returns all ٍEntities.
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/entities")
    public ResponseEntity<ServiceResponse> getAllBaseDataEntities() {
        Reflections reflections = new Reflections("com.niam.kardan.model.basedata");
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);

        return responseEntityUtil.ok(entities.stream().map(Class::getSimpleName).sorted().collect(Collectors.toList()));
    }

    /**
     * Example: GET /api/basedata/ExecutionStatus
     * Returns all ExecutionStatus records.
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{entity}")
    public ResponseEntity<ServiceResponse> getAll(@PathVariable String entity) {
        Class<? extends BaseData> type = resolveEntity(entity);
        BaseDataServiceProxy<? extends BaseData> service = factory.create(type);
        return responseEntityUtil.ok(service.getAll());
    }

    /**
     * Example: GET /api/basedata/ExecutionStatus/1
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{entity}/{id}")
    public ResponseEntity<ServiceResponse> getById(@PathVariable String entity, @PathVariable Long id) {
        Class<? extends BaseData> type = resolveEntity(entity);
        BaseDataServiceProxy<? extends BaseData> service = factory.create(type);
        return responseEntityUtil.ok(service.getById(id));
    }

    /**
     * Example: GET /api/basedata/ExecutionStatus/code/STARTED
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{entity}/code/{code}")
    public ResponseEntity<ServiceResponse> getByCode(@PathVariable String entity, @PathVariable String code) {
        Class<? extends BaseData> type = resolveEntity(entity);
        BaseDataServiceProxy<? extends BaseData> service = factory.create(type);
        return responseEntityUtil.ok(service.getByCode(code));
    }

    /**
     * Dynamically resolve BaseData entity class by name.
     * Throws 404 if class not found or not a BaseData subclass.
     */
    private Class<? extends BaseData> resolveEntity(String entity) {
        try {
            String full = "com.niam.kardan.model.basedata." + entity;
            Class<?> clazz = Class.forName(full);
            if (!BaseData.class.isAssignableFrom(clazz))
                throw new IllegalArgumentException(entity + " is not a BaseData entity");
            @SuppressWarnings("unchecked")
            Class<? extends BaseData> type = (Class<? extends BaseData>) clazz;
            return type;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Entity not found: " + entity);
        }
    }
}