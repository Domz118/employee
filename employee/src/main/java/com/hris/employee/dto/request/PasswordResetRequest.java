package com.hris.employee.dto.request;




import com.hris.employee.dto.response.UserErrorMessageResponse;
import com.hris.employee.utils.Regexp;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {

    @Email(regexp = Regexp.USER_EMAIL_REGEXP, message = UserErrorMessageResponse.EMAIL_NOT_VALID)
    private String email;

    @NotBlank(message = UserErrorMessageResponse.EMPTY_PASSWORD)
    @Size(min = 8, message = UserErrorMessageResponse.SHORT_PASSWORD)
    private String password;

    @NotBlank(message = UserErrorMessageResponse.EMPTY_PASSWORD)
    @Size(min = 8, message = UserErrorMessageResponse.SHORT_PASSWORD)
    private String password2;


}

