package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.PaginationUtils;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.PartOperationTask;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.PartOperationTaskService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/part-operation-tasks")
public class PartOperationTaskController {
    private final PartOperationTaskService partOperationTaskService;
    private final PaginationUtils paginationUtils;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @PostMapping
    public ResponseEntity<ServiceResponse> create(@RequestBody PartOperationTask task) {
        return responseEntityUtil.ok(partOperationTaskService.create(task));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> update(@PathVariable Long id, @RequestBody PartOperationTask task) {
        return responseEntityUtil.ok(partOperationTaskService.update(id, task));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> delete(@PathVariable Long id) {
        partOperationTaskService.delete(id);
        return responseEntityUtil.ok("Task deleted successfully");
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getById(@PathVariable Long id) {
        return responseEntityUtil.ok(partOperationTaskService.getById(id));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @GetMapping
    public ResponseEntity<ServiceResponse> getAll(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(partOperationTaskService.getAll(paginationUtils.pageHandler(requestParams)));
    }

    @HasPermission(PRIVILEGE.OPERATION_EXECUTION)
    @PostMapping("/{id}/complete")
    public ResponseEntity<ServiceResponse> complete(@PathVariable Long id) {
        return responseEntityUtil.ok(partOperationTaskService.markAsCompleted(id));
    }
}