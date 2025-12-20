package com.niam.kardan.config;

import com.niam.common.model.response.ServiceResponse;
import com.niam.common.utils.ResponseEntityUtil;
import com.niam.kardan.model.UserAccount;
import com.niam.kardan.model.UserType;
import com.niam.kardan.model.dto.Profile;
import com.niam.kardan.model.dto.LoginResponse;
import com.niam.kardan.service.UserAccountService;
import com.niam.usermanagement.model.payload.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    private final UserAccountService userAccountService;
    private final ResponseEntityUtil responseEntityUtil;

    @Around("execution(* com.niam.usermanagement.controller.AuthenticationController.authenticate(..))")
    public Object aroundAuthenticate(ProceedingJoinPoint joinPoint) throws Throwable {
        ResponseEntity<ServiceResponse> serviceResponse = (ResponseEntity<ServiceResponse>) joinPoint.proceed();
        AuthenticationResponse auth = (AuthenticationResponse) serviceResponse.getBody().getData();

        UserAccount account = userAccountService.getByUsername(auth.getUser().getUsername());

        Profile profile = Profile.builder()
                .types(account.getTypes().stream().map(UserType::valueOf).collect(Collectors.toSet()))
                .personnelCode(account.getPersonnelCode())
                .build();

        LoginResponse response = new LoginResponse();
        BeanUtils.copyProperties(auth, response);
        response.setProfile(profile);

        return responseEntityUtil.ok(response, serviceResponse.getHeaders());
    }
}