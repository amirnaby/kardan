package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.StopReason;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.StopReasonService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/stop-reasons")
public class StopReasonController {
    private final StopReasonService stopReasonService;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.OPERATION_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> saveStopReason(@RequestBody StopReason stopReason) {
        return responseEntityUtil.ok(stopReasonService.create(stopReason));
    }

    @HasPermission(PRIVILEGE.OPERATION_MANAGE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateStopReason(@PathVariable Long id, @RequestBody StopReason stopReason) {
        return responseEntityUtil.ok(stopReasonService.update(id, stopReason));
    }

    @HasPermission(PRIVILEGE.OPERATION_MANAGE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteStopReason(@PathVariable Long id) {
        stopReasonService.delete(id);
        return responseEntityUtil.ok("StopReason deleted successfully");
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findStopReason(@PathVariable Long id) {
        return responseEntityUtil.ok(stopReasonService.getById(id));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllStopReasons(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(stopReasonService.getAll(requestParams));
    }
}