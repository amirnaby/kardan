package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Operation;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.OperationService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/operations")
public class OperationController {
    private final OperationService operationService;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.OPERATION_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> createOperation(@RequestBody Operation Operation) {
        return responseEntityUtil.ok(operationService.create(Operation));
    }

    @HasPermission(PRIVILEGE.OPERATION_MANAGE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateOperation(@PathVariable Long id, @RequestBody Operation Operation) {
        return responseEntityUtil.ok(operationService.update(id, Operation));
    }

    @HasPermission(PRIVILEGE.OPERATION_MANAGE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteOperation(@PathVariable Long id) {
        operationService.delete(id);
        return responseEntityUtil.ok("Operation deleted successfully");
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findOperation(@PathVariable Long id) {
        return responseEntityUtil.ok(operationService.getById(id));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllOperations() {
        return responseEntityUtil.ok(operationService.getAll());
    }
}