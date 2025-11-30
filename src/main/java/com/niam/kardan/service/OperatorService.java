package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.IllegalArgumentException;
import com.niam.common.exception.IllegalStateException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.common.utils.MessageUtil;
import com.niam.kardan.model.Operator;
import com.niam.kardan.model.dto.OperatorAccount;
import com.niam.kardan.repository.OperatorRepository;
import com.niam.kardan.repository.OperatorShiftRepository;
import com.niam.usermanagement.model.entities.Role;
import com.niam.usermanagement.model.entities.User;
import com.niam.usermanagement.model.payload.request.UserDTO;
import com.niam.usermanagement.model.repository.RoleRepository;
import com.niam.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OperatorService {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final OperatorRepository operatorRepository;
    private final OperatorShiftRepository operatorShiftRepository;
    private final MessageUtil messageUtil;

    @Lazy
    @Autowired
    private OperatorService self;

    @Transactional("transactionManager")
    public Operator createOperatorAccount(OperatorAccount operatorAccount) {
        UserDTO userDTO = operatorAccount.getUser();

        Set<String> roleNames = Set.of("ROLE_OPERATOR");
        Set<String> roles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + name)))
                .map(Role::getName)
                .collect(Collectors.toSet());

        userDTO.setRoleNames(roles);

        User user = userService.createUser(userDTO);

        Operator operatorFromRequest = operatorAccount.getOperator();

        Operator operator = new Operator();
        BeanUtils.copyProperties(operatorFromRequest, operator);
        operator.setUser(user);

        return create(operator);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operator", "operators"}, allEntries = true)
    public Operator create(Operator operator) {
        if (operatorRepository.existsByUserId(operator.getUser().getId())) {
            throw new IllegalArgumentException(messageUtil.getMessage(
                    ResultResponseStatus.DUPLICATE_TRANSACTION.getDescription(), "Operator"));
        }
        return operatorRepository.save(operator);
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operator", "operators"}, allEntries = true)
    public Operator update(Long id, Operator updatedOperator) {
        Operator existing = operatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                        ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                        messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "Operator")));
        BeanUtils.copyProperties(updatedOperator, existing, "id", "user");
        return operatorRepository.save(existing);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "operator", key = "#id")
    public Operator getById(Long id) {
        return operatorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "Operator")));
    }

    @Transactional(readOnly = true, value = "transactionManager")
    @Cacheable(value = "operators")
    public List<Operator> getAll() {
        return operatorRepository.findAll();
    }

    @Transactional("transactionManager")
    @CacheEvict(value = {"operator", "operators"}, allEntries = true)
    public void delete(Long id) {
        Operator operator = self.getById(id);
        boolean hasActiveShift = operatorShiftRepository.existsByOperatorIdAndUnassignedAtIsNull(operator.getId());
        if (hasActiveShift) {
            throw new IllegalStateException(messageUtil.getMessage(
                    "error.operator.linked.activeShift", "Operator"));
        }

        operatorRepository.delete(operator);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public Operator findByUserId(Long userId) {
        return operatorRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                messageUtil.getMessage(ResultResponseStatus.ENTITY_NOT_FOUND.getDescription(), "Operator")));
    }
}