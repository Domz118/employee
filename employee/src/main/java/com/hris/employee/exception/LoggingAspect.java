package com.hris.employee.exception;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.hris.employee.resource..*(..)) || " +
            "execution(* com.hris.employee.service..*(..)) || " +
            "execution(* com.hris.employee.repository..*(..))")
    public void applicationPackagePointcut() {}

    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        StringBuilder safeArgs = new StringBuilder();

        for (Object arg : args) {
            if (arg == null) {
                safeArgs.append("null, ");
                continue;
            }

            safeArgs.append(maskSensitiveFields(arg)).append(", ");

            //  Validate ONLY DTO / Request objects
            if (isRequestOrDto(arg)) {
                validatePasswords(arg);
            }
        }

        if (safeArgs.length() > 2) {
            safeArgs.setLength(safeArgs.length() - 2);
        }

        Set<String> roles = extractRoles();

        log.info("Entering Method: {} with args: {} | Roles: {}",
                joinPoint.getSignature(),
                safeArgs,
                roles);
    }

    @After("applicationPackagePointcut()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Exiting Method: {}", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.warn("Exception in Method: {}", joinPoint.getSignature(), ex);
    }

    // ==============================
    // Helper Methods
    // ==============================

    private boolean isRequestOrDto(Object obj) {
        String className = obj.getClass().getSimpleName();
        return className.contains("request") || className.contains("dto");
    }

    private Set<String> extractRoles() {
        Set<String> roles = new HashSet<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities() != null) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                roles.add(authority.getAuthority());
            }
        }

        return roles;
    }


    private String maskSensitiveFields(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {

            // ✅ Skip all JDK classes (String, Integer, UUID, etc.)
            if (field.getType().getPackageName().startsWith("java.")) {
                continue;
            }

            try {
                field.setAccessible(true);
            } catch (Exception e) {
                continue; // cannot access, skip
            }

            String name = field.getName();
            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                value = "unable-to-read";
            }

            if (name.toLowerCase().contains("password")) {
                sb.append(name).append("=***masked***, ");
            } else {
                sb.append(name).append("=").append(value).append(", ");
            }
        }

        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    private void validatePasswords(Object obj) {

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {

            if (field.getName().toLowerCase().contains("password")) {
                field.setAccessible(true);

                try {
                    Object value = field.get(obj);

                    if (value == null || value.toString().trim().length() < 8) {
                        throw new IllegalArgumentException(
                                "Password fields must be at least 8 characters long and not empty"
                        );
                    }

                } catch (IllegalAccessException ignored) {}
            }
        }
    }
}


//package com.health.hms.common;
    //
    //import lombok.extern.slf4j.Slf4j;
    //import org.aspectj.lang.JoinPoint;
    //import org.aspectj.lang.annotation.*;
    //import org.springframework.stereotype.Component;
    //
    //import java.lang.reflect.Field;
    //
    //@Aspect
    //@Component
    //@Slf4j
    //public class LoggingAspect {
    //
    //    @Pointcut("execution(* com.health.hms.controller..*(..)) || " +
    //            "execution(* com.health.hms.service..*(..)) || " +
    //            "execution(* com.health.hms.repository..*(..))")
    //    public void applicationPackagePointcut() {}
    //
    //    @Before("applicationPackagePointcut()")
    //    public void logBefore(JoinPoint joinPoint) {
    //        Object[] args = joinPoint.getArgs();
    //        StringBuilder safeArgs = new StringBuilder();
    //
    //        for (Object arg : args) {
    //            if (arg == null) {
    //                safeArgs.append("null, ");
    //                continue;
    //            }
    //
    //            // Mask password fields dynamically
    //            String argStr = maskSensitiveFields(arg);
    //
    //            // Validate password fields if any
    //            validatePasswords(arg);
    //
    //            safeArgs.append(argStr).append(", ");
    //        }
    //
    //        if (safeArgs.length() > 2) safeArgs.setLength(safeArgs.length() - 2);
    //        log.info("Entering Method: {} with args: {}", joinPoint.getSignature(), safeArgs);
    //    }
    //
    //    @After("applicationPackagePointcut()")
    //    public void logAfter(JoinPoint joinPoint) {
    //        log.info("Exiting Method: {}", joinPoint.getSignature());
    //    }
    //
    //    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "ex")
    //    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
    //        log.warn("Exception in Method: {}", joinPoint.getSignature(), ex);
    //    }
    //
    //    // Mask any field containing "password"
    //    private String maskSensitiveFields(Object obj) {
    //        StringBuilder sb = new StringBuilder("{");
    //        Field[] fields = obj.getClass().getDeclaredFields();
    //
    //        for (Field field : fields) {
    //            field.setAccessible(true);
    //            String name = field.getName();
    //            Object value;
    //            try {
    //                value = field.get(obj);
    //            } catch (IllegalAccessException e) {
    //                value = "unable-to-read";
    //            }
    //
    //            if (name.toLowerCase().contains("password")) {
    //                sb.append(name).append("=***masked***, ");
    //            } else {
    //                sb.append(name).append("=").append(value).append(", ");
    //            }
    //        }
    //
    //        if (sb.length() > 2) sb.setLength(sb.length() - 2);
    //        sb.append("}");
    //        return sb.toString();
    //    }
    //
    //    // Validate passwords dynamically
    //    private void validatePasswords(Object obj) {
    //        Field[] fields = obj.getClass().getDeclaredFields();
    //        for (Field field : fields) {
    //            if (field.getName().toLowerCase().contains("password")) {
    //                field.setAccessible(true);
    //                try {
    //                    Object value = field.get(obj);
    //                    if (value == null || value.toString().length() < 8) {
    //                        throw new IllegalArgumentException(
    //                                "Password fields must be at least 8 characters long and not empty"
    //                        );
    //                    }
    //                } catch (IllegalAccessException ignored) {}
    //            }
    //        }
    //    }
    //}
