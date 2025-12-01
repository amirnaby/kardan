package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Part;
import com.niam.kardan.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parts")
public class PartController {
    private final PartService partService;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")

    @PostMapping
    public ResponseEntity<ServiceResponse> createPart(@RequestBody Part part) {
        return responseEntityUtil.ok(partService.create(part));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updatePart(@PathVariable Long id, @RequestBody Part part) {
        return responseEntityUtil.ok(partService.update(id, part));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deletePart(@PathVariable Long id) {
        partService.delete(id);
        return responseEntityUtil.ok("Part deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findPart(@PathVariable Long id) {
        return responseEntityUtil.ok(partService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllParts() {
        return responseEntityUtil.ok(partService.getAll());
    }
}