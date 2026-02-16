package com.hris.employee.dto.request;


import com.hris.employee.dto.response.UserErrorMessageResponse;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentPasswordResetRequest {

    @NotBlank(message = UserErrorMessageResponse.EMPTY_CURRENT_PASSWORD)
    private String currentPassword;

    @NotBlank(message = UserErrorMessageResponse.EMPTY_PASSWORD)
    @Size(min = 8, message = UserErrorMessageResponse.SHORT_PASSWORD)
    private String password;

    @NotBlank(message = UserErrorMessageResponse.EMPTY_PASSWORD_CONFIRMATION)
    @Size(min = 8, message = UserErrorMessageResponse.SHORT_PASSWORD)
    private String password2;
}

