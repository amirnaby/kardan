package com.niam.kardan.model.dto;

import com.niam.usermanagement.model.payload.request.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private UserDTO userDTO;
    private Profile profile;
}