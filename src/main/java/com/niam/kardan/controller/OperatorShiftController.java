package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.OperatorShift;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.OperatorShiftService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/operator-shifts")
public class OperatorShiftController {
    private final OperatorShiftService operatorShiftService;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> assignShift(@RequestBody OperatorShift operatorShift) {
        return responseEntityUtil.ok(operatorShiftService.create(operatorShift));
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateOperatorShift(@PathVariable Long id, @RequestBody OperatorShift operatorShift) {
        return responseEntityUtil.ok(operatorShiftService.update(id, operatorShift));
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> unassignShift(@PathVariable Long id) {
        operatorShiftService.unassign(id);
        return responseEntityUtil.ok("OperatorShift unassigned successfully");
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findOperatorShift(@PathVariable Long id) {
        return responseEntityUtil.ok(operatorShiftService.getById(id));
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllOperatorShifts(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(operatorShiftService.getAll(requestParams));
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @GetMapping("/active/{operatorId}")
    public ResponseEntity<ServiceResponse> findActiveShifts(@PathVariable Long operatorId) {
        return responseEntityUtil.ok(operatorShiftService.findActiveShiftsByOperator(operatorId));
    }
}