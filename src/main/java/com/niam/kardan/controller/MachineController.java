package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Machine;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.MachineService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/machines")
public class MachineController {
    private final MachineService machineService;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.MACHINE_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> createMachine(@RequestBody Machine machine) {
        return responseEntityUtil.ok(machineService.create(machine));
    }

    @HasPermission(PRIVILEGE.MACHINE_MANAGE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateMachine(@PathVariable Long id, @RequestBody Machine machine) {
        return responseEntityUtil.ok(machineService.update(id, machine));
    }

    @HasPermission(PRIVILEGE.MACHINE_MANAGE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteMachine(@PathVariable Long id) {
        machineService.delete(id);
        return responseEntityUtil.ok("Machine deleted successfully");
    }

    @HasPermission(PRIVILEGE.MACHINE_VIEW)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findMachine(@PathVariable Long id) {
        return responseEntityUtil.ok(machineService.getById(id));
    }

    @HasPermission(PRIVILEGE.MACHINE_VIEW)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllMachines(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(machineService.getAll(requestParams));
    }
}