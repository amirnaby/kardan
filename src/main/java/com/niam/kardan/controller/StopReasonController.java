package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.StopReason;
import com.niam.kardan.service.StopReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/stop-reasons")
public class StopReasonController {
    private final StopReasonService stopReasonService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> saveStopReason(@RequestBody StopReason stopReason) {
        return responseEntityUtil.ok(stopReasonService.create(stopReason));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateStopReason(@PathVariable Long id, @RequestBody StopReason stopReason) {
        return responseEntityUtil.ok(stopReasonService.update(id, stopReason));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteStopReason(@PathVariable Long id) {
        stopReasonService.delete(id);
        return responseEntityUtil.ok("StopReason deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findStopReason(@PathVariable Long id) {
        return responseEntityUtil.ok(stopReasonService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllStopReasons() {
        return responseEntityUtil.ok(stopReasonService.getAll());
    }
}