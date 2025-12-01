package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.OperatorMachine;
import com.niam.kardan.service.OperatorMachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/operator-machines")
public class OperatorMachineController {
    private final OperatorMachineService operatorMachineService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> assignMachine(@RequestBody OperatorMachine operatorMachine) {
        return responseEntityUtil.ok(operatorMachineService.create(operatorMachine));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateOperatorMachine(@PathVariable Long id, @RequestBody OperatorMachine operatorMachine) {
        return responseEntityUtil.ok(operatorMachineService.update(id, operatorMachine));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> unassignMachine(@PathVariable Long id) {
        operatorMachineService.unassign(id);
        return responseEntityUtil.ok("OperatorMachine unassigned successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findOperatorMachine(@PathVariable Long id) {
        return responseEntityUtil.ok(operatorMachineService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllOperatorMachines() {
        return responseEntityUtil.ok(operatorMachineService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/active/{operatorId}")
    public ResponseEntity<ServiceResponse> findActiveMachines(@PathVariable Long operatorId) {
        return responseEntityUtil.ok(operatorMachineService.findActiveMachinesByOperator(operatorId));
    }
}