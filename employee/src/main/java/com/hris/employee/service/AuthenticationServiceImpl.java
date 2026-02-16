package com.hris.employee.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hris.employee.exception.handler.ApiRequestException;
import com.hris.employee.exception.handler.InputFieldException;
import com.hris.employee.exception.handler.UserNotFoundException;

import com.hris.employee.configuration.ApplicationProperties;
import com.hris.employee.dto.request.LoginRequest;
import com.hris.employee.dto.request.UserRequest;
import com.hris.employee.dto.response.JwtResponse;
import com.hris.employee.dto.response.PageResponse;
import com.hris.employee.dto.response.UserResponse;

import com.hris.employee.entity.User;
import com.hris.employee.entity.enums.ERole;
import com.hris.employee.dto.response.UserErrorMessageResponse;
import com.hris.employee.exception.handler.FieldValidationException;
import com.hris.employee.repository.UserRepository;
import com.hris.employee.service.interfce.AuthenticationService;
import com.hris.employee.utils.Mappers;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;


import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final  PasswordEncoder passwordEncoder;
    private final ApplicationProperties properties;
    private final AuthenticationManager  authenticationManager;

    private static final String  messageSuccess = "SUCCESS PASSWORD CHANGED";
    private static final String  messageResetPassword = "RESET_PASSWORD_CODE_IS_SEND";


    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        log.info("In getAllUsers()");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        log.info("{} users found", users.getTotalElements());
        return Mappers.fromPageOfUsers(users);
    }

    @Override
    public PageResponse<UserResponse> searchUsers(String query, int page, int size) {
        log.info("In searchUsers()");
        Pageable pageable = PageRequest.of(page, size);
        String keyword = "%"+query+"%";
        Page<User> users = userRepository.search(keyword, pageable);
        log.info("{} users found.", users.getTotalElements());
        return Mappers.fromPageOfUsers(users);
    }



    @Override
    public JwtResponse authenticate(@NotNull LoginRequest dto) {
        log.info("In authenticate()");

        // Authenticate user
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        if (authenticationResponse.isAuthenticated()) {
            User user = getUserUsername(dto.getUsername());

            log.info("Authentication successful");

            // Generate JWT
            String jwt = generateToken(user);
            isAccountExpired(user.getLastLoginDate());
            // Update last login
            updateUserLastLoginDate(user);

            // Extract roles as List<String>
            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getName().name()) // ERole -> String
                    .toList();

            // Return JwtResponse
            return new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    roles,
                    user.getIsAccountLocked() // include account lock status
            );
        } else {
            log.error("Authentication failed for user: {}", dto.getUsername());
            throw new UserNotFoundException("User not authenticated");
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = {"user", "userPage, search"}, allEntries = true)
    public UserResponse updateUser(Long id, UserRequest dto) {
        log.info("In updateUser()");
        User user = findUserById(id);
        validationBeforeUpdate(user,dto);
        updateUserFields(user,dto);
        User savedUser = userRepository.save(user);
        log.info("User with id: {} updated", savedUser.getId());
        return Mappers.fromUser(savedUser);
    }



    @Override
    @Transactional
    public String passwordReset(String email, String password, String password2) {
        checkMatchPasswords(password, password2);
        User user = userRepository.getUserByEmail(email, User.class)
                .orElseThrow(() -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("email", UserErrorMessageResponse.EMAIL_NOT_FOUND);
                    return new InputFieldException(HttpStatus.NOT_FOUND, errorMap);
                });

        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordResetCode(passwordEncoder.encode(password2));

        userRepository.save(user);

        return messageSuccess;


    }




    @Override
    public UserResponse getUserByUsername(String username) {
        log.info("In getUserByUsername()");
        User user = findUserByUsername(username);
        log.info("User with name {} found successfully", username);
        return Mappers.fromUser(user);
    }
    private void validationBeforeUpdate(@NotNull User user, @NotNull UserRequest dto) {
        List<String> errors = new ArrayList<>();
        if(!user.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            errors.add("Username already exists");
        }
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            errors.add("Email already exists");
        }
        if(!errors.isEmpty()){
            throw new FieldValidationException("Invalid username or email", errors);
        }
    }

    private void updateUserFields(@NotNull User user, @NotNull UserRequest userRequest){
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
    }

    private void updateUserLastLoginDate(@NotNull User user) {
        user.setLastLoginDate(LocalDateTime.now());
         userRepository.save(user);

    }
    private Boolean isAccountExpired(LocalDateTime lastLoginDate){
        return lastLoginDate != null && lastLoginDate.isBefore(LocalDateTime.now().minusMonths(60));
    }

    private String generateToken(@NotNull User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name()) // directly get String from Role -> ERole
                .collect(Collectors.toSet());
        return createJwt(user, roleNames);
    }

    private User getUserUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found with username or email: {}", username);
            return new UserNotFoundException("User not found");
        });
    }

    private Set<String> getListOfNamesOfRoles(Set<ERole> roles) {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }
        return roles.stream()
                .map(Enum::name) // returns String
                .collect(Collectors.toSet());
    }



    private String createJwt(@NotNull User user, @NotNull Set<String> roles) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getJwtSecret());
        Date expiration = new Date(System.currentTimeMillis() + properties.getJwtExpirationMs());
        return JWT.create()
                .withSubject(user.getUsername())
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withClaim("username", user.getUsername())
                .withClaim("id", user.getId())
                .withExpiresAt(expiration)
                .sign(algorithm);
    }



    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow( () -> new UserNotFoundException(String.format("User with username %s not found", username)) );
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", id)));
    }




    private void checkMatchPasswords(String password, String password2) {
        if (password == null || !password.equals(password2)) {
            processPasswordException(UserErrorMessageResponse.PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }
    }

    private void processPasswordException(String exceptionMessage, HttpStatus status) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("password", exceptionMessage);
        throw new InputFieldException(status, errorMap);
    }


    private String getExistingEmail(String email) {
        userRepository.getUserByEmail(email, User.class)
                .orElseThrow(() -> new ApiRequestException(UserErrorMessageResponse.EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND));
        return messageResetPassword;
    }



}

