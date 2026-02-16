package com.hris.employee.resource;

import com.hris.employee.dto.request.*;
import com.hris.employee.dto.response.*;
import com.hris.employee.entity.Role;
import com.hris.employee.entity.User;
import com.hris.employee.entity.enums.ERole;
import com.hris.employee.repository.RoleRepository;
import com.hris.employee.repository.UserRepository;
import com.hris.employee.service.interfce.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value="/auth")
public class AuthController {


    private final UserRepository userRepository;

    private final RoleRepository roleRepository;


    private final PasswordEncoder encoder;

    private final AuthenticationService authenticationService;



    @PostMapping(value="/login")
    public JwtResponse authenticate(@RequestBody LoginRequest requestDTO) {
        return authenticationService.authenticate(requestDTO);
    }




    @PostMapping(value="/signup")
    @CacheEvict(value = {"user", "userPage, search"}, allEntries = true)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),signUpRequest.getEmail());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setIsAccountLocked(false);
        user.setLastLoginDate(LocalDateTime.now());
        user.setRoles(roles);
        userRepository.save(user);


        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @PostMapping(value="/reset")
    public ResponseEntity<String> passwordReset(@Valid @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(
                authenticationService.passwordReset(
                        request.getEmail(),
                        request.getPassword(),
                        request.getPassword2()
                )
        );
    }
    @GetMapping(value="/all")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return authenticationService.getAllUsers(page, size);
    }



    @GetMapping(value="/search")
    public PageResponse<UserResponse> searchUsers(@RequestParam(defaultValue = " ") String query,
                                                  @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return authenticationService.searchUsers(query, page, size);
    }


    @GetMapping(value="/user/me")
    public UserResponse getUserByUsername() {
        String username = getCurrentUsername();
        return authenticationService.getUserByUsername(username);
    }


    @PutMapping(value="/update/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest dto) {
        return authenticationService.updateUser(id, dto);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }


}
