package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Operation;
import com.niam.kardan.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/operations")
public class OperationController {
    private final OperationService operationService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> createOperation(@RequestBody Operation Operation) {
        return responseEntityUtil.ok(operationService.create(Operation));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateOperation(@PathVariable Long id, @RequestBody Operation Operation) {
        return responseEntityUtil.ok(operationService.update(id, Operation));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteOperation(@PathVariable Long id) {
        operationService.delete(id);
        return responseEntityUtil.ok("Operation deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findOperation(@PathVariable Long id) {
        return responseEntityUtil.ok(operationService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllOperations() {
        return responseEntityUtil.ok(operationService.getAll());
    }
}