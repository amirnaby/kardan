package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.PaginationUtils;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.Part;
import com.niam.kardan.model.enums.PRIVILEGE;
import com.niam.kardan.service.PartService;
import com.niam.usermanagement.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parts")
public class PartController {
    private final PartService partService;
    private final PaginationUtils paginationUtils;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.PART_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> createPart(@RequestBody Part part) {
        return responseEntityUtil.ok(partService.create(part));
    }

    @HasPermission(PRIVILEGE.PART_MANAGE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updatePart(@PathVariable Long id, @RequestBody Part part) {
        return responseEntityUtil.ok(partService.update(id, part));
    }

    @HasPermission(PRIVILEGE.PART_MANAGE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deletePart(@PathVariable Long id) {
        partService.delete(id);
        return responseEntityUtil.ok("Part deleted successfully");
    }

    @HasPermission(PRIVILEGE.PART_VIEW)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findPart(@PathVariable Long id) {
        return responseEntityUtil.ok(partService.getById(id));
    }

    @HasPermission(PRIVILEGE.PART_VIEW)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllParts(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(partService.getAll(paginationUtils.pageHandler(requestParams)));
    }
}