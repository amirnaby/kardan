package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.OperatorShift;
import com.niam.kardan.service.OperatorShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/operator-shifts")
public class OperatorShiftController {
    private final OperatorShiftService operatorShiftService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> assignShift(@RequestBody OperatorShift operatorShift) {
        return responseEntityUtil.ok(operatorShiftService.create(operatorShift));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateOperatorShift(@PathVariable Long id, @RequestBody OperatorShift operatorShift) {
        return responseEntityUtil.ok(operatorShiftService.update(id, operatorShift));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> unassignShift(@PathVariable Long id) {
        operatorShiftService.unassign(id);
        return responseEntityUtil.ok("OperatorShift unassigned successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findOperatorShift(@PathVariable Long id) {
        return responseEntityUtil.ok(operatorShiftService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllOperatorShifts() {
        return responseEntityUtil.ok(operatorShiftService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/active/{operatorId}")
    public ResponseEntity<ServiceResponse> findActiveShifts(@PathVariable Long operatorId) {
        return responseEntityUtil.ok(operatorShiftService.findActiveShiftsByOperator(operatorId));
    }
}