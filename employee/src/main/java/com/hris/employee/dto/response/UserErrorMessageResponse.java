package com.hris.employee.dto.response;

public class UserErrorMessageResponse {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_NOT_FOUND = "Email not found";
    public static final String EMAIL_NOT_VALID = "Please enter a valid email address.";
    public static final String EMAIL_HAS_ALREADY_BEEN_TAKEN = "Email has already been taken.";
    public static final String NAME_NOT_VALID = "Please enter a valid name.";
    public static final String PASSWORDS_NOT_MATCH = "Passwords do not match.";
    public static final String INCORRECT_PASSWORD = "The password you entered was incorrect.";
    public static final String INVALID_PASSWORD_RESET_CODE = "Password reset code is invalid!";
    public static final String PASSWORD_LENGTH_ERROR = "Your password needs to be at least 8 characters";
    public static final String EMPTY_PASSWORD = "Password cannot be empty.";
    public static final String EMPTY_CURRENT_PASSWORD = "Current password cannot be empty.";
    public static final String EMPTY_PASSWORD_CONFIRMATION = "Password confirmation cannot be empty.";
    public static final String SHORT_PASSWORD = "Your password needs to be at least 8 characters. Please enter a longer one.";
    public static final String INCORRECT_USERNAME_LENGTH = "Incorrect username length";
    public static final String INVALID_PHONE_NUMBER = "Not valid phone number";
}
