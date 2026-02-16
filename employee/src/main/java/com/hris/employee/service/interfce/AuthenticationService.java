package com.hris.employee.service.interfce;


import com.hris.employee.dto.request.LoginRequest;
import com.hris.employee.dto.request.UserRequest;
import com.hris.employee.dto.response.JwtResponse;
import com.hris.employee.dto.response.PageResponse;
import com.hris.employee.dto.response.UserResponse;


public interface AuthenticationService {

    JwtResponse authenticate(LoginRequest loginRequest);
    PageResponse<UserResponse> getAllUsers(int page, int size);
    PageResponse<UserResponse> searchUsers(String query, int page, int size);
    UserResponse updateUser(Long id, UserRequest request);
     UserResponse getUserByUsername(String username);
    String passwordReset(String email, String password, String password2);
}

