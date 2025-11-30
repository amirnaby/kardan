package com.niam.kardan.model.dto;

import com.niam.kardan.model.Operator;
import com.niam.usermanagement.model.payload.request.UserDTO;
import lombok.Data;

@Data
public class OperatorAccount {
    private UserDTO user;
    private Operator operator;
}