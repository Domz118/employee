package com.hris.employee.security;


import com.hris.employee.entity.Role;
import com.hris.employee.entity.User;
import com.hris.employee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;



        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            log.info("In loadUserByUsername()");

            User user = findUserByUsername(username);

            Set<GrantedAuthority> grantedAuthorities = getAuthorities(user.getRoles());

            log.info("User loaded successfully: {}", user.getUsername());

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    grantedAuthorities
            );
        }

        private User findUserByUsername(final String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        }

        private @NotNull Set<GrantedAuthority> getAuthorities(final @NotNull Set<Role> roles) {
            // Using Java Streams for clarity and conciseness
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toSet());
        }
    }


