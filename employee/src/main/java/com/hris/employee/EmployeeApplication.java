package com.hris.employee;

import com.hris.employee.entity.Department;
import com.hris.employee.entity.Employee;
import com.hris.employee.entity.Role;
import com.hris.employee.entity.User;
import com.hris.employee.entity.enums.ERole;
import com.hris.employee.repository.DepartmentRepository;
import com.hris.employee.repository.EmployeeRepository;
import com.hris.employee.repository.RoleRepository;
import com.hris.employee.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.cache.annotation.EnableCaching;


@Configuration
@Slf4j
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories("com.hris.employee.repository")
@EnableCaching
@SpringBootApplication
public class EmployeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeApplication.class, args);
	}

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        return args -> {

            // ROLE CREATION
            // Check if roles exist
            if (roleRepository.count() == 0) {
                log.info("No roles found, creating default roles...");
                try {
                    Role userRole = Role.builder().name(ERole.ROLE_USER).build();
                    Role adminRole = Role.builder().name(ERole.ROLE_ADMIN).build();
                    roleRepository.save(userRole);
                    roleRepository.save(adminRole);
                    log.info("Roles 'USER' and 'ADMIN' created successfully");
                } catch (Exception e) {
                    log.error("Error while creating roles", e);
                }
            }

            // Check if super admin exists
            User superAdmin = userRepository.findByUsername("admin").orElse(null);
            if (superAdmin == null) {
                log.info("No super admin found, creating one...");
                try {
                    superAdmin = new User();
                    superAdmin.setUsername("admin");
                    superAdmin.setEmail("admin@gmail.com");
                    superAdmin.setIsAccountLocked(false);
                    superAdmin.setLastLoginDate(LocalDateTime.now());

                    superAdmin.setPassword(passwordEncoder.encode("admin123"));

                    // Assign roles
                    Set<Role> roles = new HashSet<>();
                  /*TODO  Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                           .orElseThrow(() -> new RuntimeException("Error: Role 'ADMIN' not found."));
                    roles.add(adminRole); */
                    Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role 'USER' not found."));
                    roles.add(userRole);

                    superAdmin.setRoles(roles);

                    User savedUser = userRepository.save(superAdmin);
                    log.info("SUPER ADMIN created successfully with save '{}'", savedUser);

                } catch (Exception e) {
                    log.error("Error while creating super admin user", e);
                }
            }

            User users = userRepository.findByUsername("user").orElse(null);
            if (users == null) {
                log.info("No user  found, creating one...");
                try {
                    users = new User();
                    users.setUsername("user");
                    users.setEmail("user@gmail.com");
                    users.setIsAccountLocked(false);
                    users.setLastLoginDate(LocalDateTime.now());

                    users.setPassword(passwordEncoder.encode("user123"));

                    // Assign roles
                    Set<Role> roles1 = new HashSet<>();
                    /*TODO  Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role 'ADMIN' not found."));
                    roles.add(adminRole); */
                    Role userRole1 = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role 'USER' not found."));
                    roles1.add(userRole1);

                    users.setRoles(roles1);

                    User savedUser1 = userRepository.save(users);
                    log.info("SUPER User created successfully with save '{}'", savedUser1);

                } catch (Exception e) {
                    log.error("Error while creating super admin user", e);
                }
            }

            // Create default department if not exists
            Department department = departmentRepository
                    .findByDeptName("HR")
                    .orElseGet(() -> {
                        log.info("HR department not found, creating...");
                        Department newDept = Department.builder()
                                .deptName("HR")
                                .build();
                        return departmentRepository.save(newDept);
                    });


        // Create default employee if not exists
            employeeRepository
                    .findByFirstNameAndLastName("USER_FIRSTNAME", "USER_LASTNAME")
                    .ifPresentOrElse(
                            emp -> log.info("Default employee already exists"),
                            () -> {
                                log.info("Creating default employee...");

                                try {
                                    Employee employee = Employee.builder()
                                            .empName("USER_EMP_NAME")
                                            .firstName("USER_FIRSTNAME")
                                            .lastName("USER_LASTNAME")
                                            .empMobileNo(1234887610L)
                                            .empAdd("Streets")
                                            .department(department)
                                            .build();

                                    Employee saved = employeeRepository.save(employee);

                                    log.info("Employee created successfully: {}", saved.getEmpId());

                                } catch (Exception e) {
                                    log.error("Error while creating default employee", e);
                                }
                            }
                    );

        };
    }
}



