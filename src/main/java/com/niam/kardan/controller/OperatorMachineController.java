package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.PaginationUtils;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.OperatorMachine;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.OperatorMachineService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/operator-machines")
public class OperatorMachineController {
    private final OperatorMachineService operatorMachineService;
    private final PaginationUtils paginationUtils;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @PostMapping
    public ResponseEntity<ServiceResponse> assignMachine(@RequestBody OperatorMachine operatorMachine) {
        return responseEntityUtil.ok(operatorMachineService.create(operatorMachine));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateOperatorMachine(@PathVariable Long id, @RequestBody OperatorMachine operatorMachine) {
        return responseEntityUtil.ok(operatorMachineService.update(id, operatorMachine));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> unassignMachine(@PathVariable Long id) {
        operatorMachineService.unassign(id);
        return responseEntityUtil.ok("OperatorMachine unassigned successfully");
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findOperatorMachine(@PathVariable Long id) {
        return responseEntityUtil.ok(operatorMachineService.getById(id));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllOperatorMachines(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(operatorMachineService.getAll(paginationUtils.pageHandler(requestParams)));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping("/active/{operatorId}")
    public ResponseEntity<ServiceResponse> findActiveMachines(@PathVariable Long operatorId) {
        return responseEntityUtil.ok(operatorMachineService.findActiveMachinesByOperator(operatorId));
    }
}