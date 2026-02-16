package com.hris.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserPrincipalResponse {
    private String username;
    private List<String> roles;
}

