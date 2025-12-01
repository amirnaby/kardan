package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Operator;
import com.niam.kardan.model.dto.OperatorAccount;
import com.niam.kardan.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/operators")
public class OperatorController {
    private final OperatorService operatorService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @PostMapping
    public ResponseEntity<ServiceResponse> createOperator(@Validated @RequestBody OperatorAccount operatorAccount) {
        return responseEntityUtil.ok(operatorService.createOperatorAccount(operatorAccount));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateOperator(@PathVariable Long id, @RequestBody Operator operator) {
        return responseEntityUtil.ok(operatorService.update(id, operator));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteOperator(@PathVariable Long id) {
        operatorService.delete(id);
        return responseEntityUtil.ok("Operator deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findOperator(@PathVariable Long id) {
        return responseEntityUtil.ok(operatorService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllOperators() {
        return responseEntityUtil.ok(operatorService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ServiceResponse> findOperatorByUserId(@PathVariable Long userId) {
        return responseEntityUtil.ok(operatorService.findByUserId(userId));
    }
}