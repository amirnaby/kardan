package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Shift;
import com.niam.kardan.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/shifts")
public class ShiftController {
    private final ShiftService shiftService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> createShift(@RequestBody Shift shift) {
        return responseEntityUtil.ok(shiftService.create(shift));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateShift(@PathVariable Long id, @RequestBody Shift shift) {
        return responseEntityUtil.ok(shiftService.update(id, shift));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteShift(@PathVariable Long id) {
        shiftService.delete(id);
        return responseEntityUtil.ok("Shift deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findShift(@PathVariable Long id) {
        return responseEntityUtil.ok(shiftService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllShifts() {
        return responseEntityUtil.ok(shiftService.getAll());
    }
}