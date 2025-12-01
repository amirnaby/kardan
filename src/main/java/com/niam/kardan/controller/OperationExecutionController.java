package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.OperationExecution;
import com.niam.kardan.model.OperationStop;
import com.niam.kardan.service.OperationExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/executions")
public class OperationExecutionController {
    private final OperationExecutionService operationExecutionService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @PostMapping("/tasks/{taskId}/claim")
    public ResponseEntity<ServiceResponse> claimAndStart(@PathVariable Long taskId, @RequestParam Long operatorId,
                                                         @RequestParam Long machineId) {
        OperationExecution exec = operationExecutionService.claimAndStartTask(taskId, operatorId, machineId);
        return responseEntityUtil.ok(exec);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @PostMapping("/{executionId}/stop")
    public ResponseEntity<ServiceResponse> stopExecution(@PathVariable Long executionId, @RequestParam Long stopReasonId,
                                                         @RequestParam(required = false) String comment) {
        OperationStop stop = operationExecutionService.stopExecution(executionId, stopReasonId, comment);
        return responseEntityUtil.ok(stop);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @PostMapping("/{executionId}/finish")
    public ResponseEntity<ServiceResponse> finishExecution(@PathVariable Long executionId) {
        OperationExecution exec = operationExecutionService.finishExecution(executionId);
        return responseEntityUtil.ok(exec);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getExecution(@PathVariable Long id) {
        return responseEntityUtil.ok(operationExecutionService.getById(id));
    }
}