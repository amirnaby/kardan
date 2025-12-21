package com.niam.kardan.util;

import com.niam.kardan.model.UserAccount;
import com.niam.kardan.model.enums.UserType;
import com.niam.kardan.model.dto.AccountDTO;
import com.niam.kardan.model.dto.Profile;
import com.niam.usermanagement.utils.UserDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountMapper {
    private final UserDTOMapper userDTOMapper;

    public UserAccount AccountDTOToUserAccount(AccountDTO accountDTO) {
        UserAccount userAccount = new UserAccount() {
        };
        userAccount.setPersonnelCode(accountDTO.getProfile().getPersonnelCode());
        userAccount.setUser(userDTOMapper.userDTOToUser(accountDTO.getUserDTO()));
        return userAccount;
    }

    public AccountDTO UserAccountToAccountDTO(UserAccount userAccount) {
        return AccountDTO.builder()
                .profile(Profile.builder()
                        .personnelCode(userAccount.getPersonnelCode())
                        .types(userAccount.getTypes().stream().map(UserType::valueOf).collect(Collectors.toSet()))
                        .build())
                .userDTO(userDTOMapper.userToUserDTO(userAccount.getUser()))
                .build();
    }
}
