package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.IllegalStateException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.common.utils.PaginationUtils;
import com.niam.kardan.model.UserAccount;
import com.niam.kardan.model.dto.AccountDTO;
import com.niam.kardan.repository.OperatorShiftRepository;
import com.niam.kardan.repository.UserAccountRepository;
import com.niam.kardan.util.UserAccountMapper;
import com.niam.usermanagement.model.entities.User;
import com.niam.usermanagement.service.UserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserAccountService {
    private final UserService userService;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final OperatorShiftRepository operatorShiftRepository;
    private final PaginationUtils paginationUtils;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private UserAccountService self;

    @Transactional(value = "transactionManager")
    @CacheEvict(value = {"userAccount", "userAccounts"}, allEntries = true)
    public void create(AccountDTO accountDTO) {
        User user;
        try {
            user = userService.createUser(accountDTO.getUserDTO());
        } catch (DataIntegrityViolationException e) {
            user = userService.getUserByUsername(accountDTO.getUserDTO().getUsername());
        }
        Set<String> roles = accountDTO.getProfile().getTypes().stream().map(
                t -> "ROLE_" + t.name()).collect(Collectors.toSet());
        userService.updateRoles(user.getUsername(), roles);
        userAccountRepository.save(UserAccount.builder()
                .user(user)
                .types(accountDTO.getProfile().getTypes().stream().map(Enum::name).collect(Collectors.toSet()))
                .personnelCode(accountDTO.getProfile().getPersonnelCode())
                .build()
        );
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"userAccount", "userAccounts"}, allEntries = true)
    public void update(String username, AccountDTO updatedUserAccount) {
        UserAccount existing = self.getByUsername(username);
        BeanUtils.copyProperties(updatedUserAccount, existing, "id", "user");
        userAccountRepository.save(existing);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"userAccount", "userAccounts"}, allEntries = true)
    public void delete(String username) {
        UserAccount userAccount = self.getByUsername(username);
        boolean hasActiveShift = operatorShiftRepository.existsByOperatorIdAndUnassignedAtIsNull(userAccount.getId());
        if (hasActiveShift) {
            throw new IllegalStateException(messageUtil.getMessage(
                    "error.userAccount.linked.activeShift", "UserAccount"));
        }

        userAccountRepository.delete(userAccount);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "userAccounts")
    public List<AccountDTO> getAll(Map<String, Object> requestParams) {
        PageRequest pageRequest = paginationUtils.pageHandler(requestParams);
        Specification<UserAccount> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestParams.get("personnelCode") != null)
                predicates.add(criteriaBuilder.equal(root.get("personnelCode"), requestParams.remove("personnelCode")));
            if (requestParams.get("username") != null)
                predicates.add(criteriaBuilder.equal(root.get("userId").get("username"), requestParams.remove("username")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return userAccountRepository.findAll(specification, pageRequest).stream()
                .map(userAccountMapper::UserAccountToAccountDTO)
                .toList();
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "userAccount", key = "#id")
    public UserAccount getById(Long id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "UserAccount")));
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "userAccount", key = "#userId")
    public UserAccount getByUserId(Long userId) {
        return userAccountRepository.findByUser_id(userId).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "UserAccount")));
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "userAccount", key = "#username")
    public UserAccount getByUsername(String username) {
        User user = userService.getUserByUsername(username);
        return getByUserId(user.getId());
    }

    public boolean existsByUsername(String username) {
        return userAccountRepository.existsByUser_username(username);
    }
}