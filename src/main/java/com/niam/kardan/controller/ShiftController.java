package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Shift;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.ShiftService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/shifts")
public class ShiftController {
    private final ShiftService shiftService;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> createShift(@RequestBody Shift shift) {
        return responseEntityUtil.ok(shiftService.create(shift));
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateShift(@PathVariable Long id, @RequestBody Shift shift) {
        return responseEntityUtil.ok(shiftService.update(id, shift));
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteShift(@PathVariable Long id) {
        shiftService.delete(id);
        return responseEntityUtil.ok("Shift deleted successfully");
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findShift(@PathVariable Long id) {
        return responseEntityUtil.ok(shiftService.getById(id));
    }

    @HasPermission(PRIVILEGE.SHIFT_MANAGE)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllShifts(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(shiftService.getAll(requestParams));
    }
}