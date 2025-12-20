package com.niam.kardan.model.dto;

import com.niam.kardan.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private Set<UserType> types;
    private String personnelCode;
}