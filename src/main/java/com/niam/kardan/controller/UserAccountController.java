package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.dto.AccountDTO;
import com.niam.kardan.service.UserAccountService;
import com.niam.kardan.util.UserAccountMapper;
import com.niam.usermanagement.annotation.HasPermission;
import com.niam.usermanagement.model.enums.PRIVILEGE;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/userAccounts")
public class UserAccountController {
    private final UserAccountService userAccountService;
    private final UserAccountMapper userAccountMapper;
    private final ResponseEntityUtil responseEntityUtil;

    @HasPermission(PRIVILEGE.USER_MANAGE)
    @PostMapping
    public ResponseEntity<ServiceResponse> createUserAccount(@Validated @RequestBody AccountDTO accountDTO) {
        userAccountService.create(accountDTO);
        return responseEntityUtil.ok("UserAccount created successfully");
    }

    @HasPermission(PRIVILEGE.USER_MANAGE)
    @PutMapping("/{username}")
    public ResponseEntity<ServiceResponse> updateUserAccount(@PathVariable String username, @RequestBody AccountDTO accountDTO) {
        userAccountService.update(username, accountDTO);
        return responseEntityUtil.ok("UserAccount updated successfully");
    }

    @HasPermission(PRIVILEGE.USER_MANAGE)
    @DeleteMapping("/{username}")
    public ResponseEntity<ServiceResponse> deleteUserAccount(@PathVariable String username) {
        userAccountService.delete(username);
        return responseEntityUtil.ok("UserAccount deleted successfully");
    }

    @HasPermission(PRIVILEGE.USER_MANAGE)
    @GetMapping("/{username}")
    public ResponseEntity<ServiceResponse> findUserAccount(@PathVariable String username) {
        return responseEntityUtil.ok(userAccountMapper.UserAccountToAccountDTO(userAccountService.getByUsername(username)));
    }

    @HasPermission(PRIVILEGE.USER_MANAGE)
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllUserAccounts(@RequestParam Map<String, Object> requestParams) {
        return responseEntityUtil.ok(userAccountService.getAll(requestParams));
    }
}