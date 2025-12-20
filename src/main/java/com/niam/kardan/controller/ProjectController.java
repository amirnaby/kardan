package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Project;
import com.niam.kardan.service.ProjectService;
import com.niam.common.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final PaginationUtils paginationUtils;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> createProject(@RequestBody Project project) {
        return responseEntityUtil.ok(projectService.create(project));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateProject(@PathVariable Long id, @RequestBody Project project) {
        return responseEntityUtil.ok(projectService.update(id, project));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return responseEntityUtil.ok("Project deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findProject(@PathVariable Long id) {
        return responseEntityUtil.ok(projectService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllProjects(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(projectService.getAll(paginationUtils.pageHandler(requestParams)));
    }
}