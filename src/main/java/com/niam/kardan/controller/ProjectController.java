package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Project;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.ProjectService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.PROJECT_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> createProject(@RequestBody Project project) {
        return responseEntityUtil.ok(projectService.create(project));
    }

    @HasPermission(PRIVILEGE.PROJECT_MANAGE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateProject(@PathVariable Long id, @RequestBody Project project) {
        return responseEntityUtil.ok(projectService.update(id, project));
    }

    @HasPermission(PRIVILEGE.PROJECT_MANAGE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return responseEntityUtil.ok("Project deleted successfully");
    }

    @HasPermission(PRIVILEGE.PROJECT_VIEW)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findProject(@PathVariable Long id) {
        return responseEntityUtil.ok(projectService.getById(id));
    }

    @HasPermission(PRIVILEGE.PROJECT_VIEW)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllProjects(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(projectService.getAll(requestParams));
    }
}