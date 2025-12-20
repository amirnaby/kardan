package com.niam.kardan.controller;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.dto.AccountDTO;
import com.niam.kardan.service.UserAccountService;
import com.niam.kardan.util.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/userAccounts")
public class UserAccountController {
    private final UserAccountService userAccountService;
    private final UserAccountMapper userAccountMapper;
    private final ResponseEntityUtil responseEntityUtil;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> createUserAccount(@Validated @RequestBody AccountDTO accountDTO) {
        userAccountService.create(accountDTO);
        return responseEntityUtil.ok("UserAccount created successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{username}")
    public ResponseEntity<ServiceResponse> updateUserAccount(@PathVariable String username, @RequestBody AccountDTO accountDTO) {
        userAccountService.update(username, accountDTO);
        return responseEntityUtil.ok("UserAccount updated successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{username}")
    public ResponseEntity<ServiceResponse> deleteUserAccount(@PathVariable String username) {
        userAccountService.delete(username);
        return responseEntityUtil.ok("UserAccount deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping("/{username}")
    public ResponseEntity<ServiceResponse> findUserAccount(@PathVariable String username) {
        return responseEntityUtil.ok(userAccountMapper.UserAccountToAccountDTO(userAccountService.getByUsername(username)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    @GetMapping
    public ResponseEntity<ServiceResponse> findAllUserAccounts() {
        return responseEntityUtil.ok(userAccountService.getAll());
    }
}