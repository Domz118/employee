//package com.health.hms.service;
//
//import com.health.hms.model.User;
//import io.github.bucket4j.*;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class UserRateLimiterService {
//
//    private final ConcurrentHashMap<Long, Bucket> userBuckets = new ConcurrentHashMap<>();
//
//    // Create bucket with role-based limits
//    private Bucket createNewBucket(Set<String> roles) {
//        Bandwidth limit;
//        //ROLE_DOCTOR
//        if (roles.contains("ROLE_ADMIN") && roles.contains("ROLE_DOCTOR")) {
//            limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofSeconds(10))); // Higher limit for doctors
//        } else {
//            limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(10))); // Default for users
//        }
//        return Bucket4j.builder().addLimit(limit).build();
//    }
//
//    public boolean tryConsume(User user, Set<String> roles) {
//        if (user == null || user.getId() == null) {
//            throw new IllegalArgumentException("User or User ID cannot be null");
//        }
//
//        Bucket bucket = userBuckets.computeIfAbsent(
//                user.getId(),
//                id -> createNewBucket(roles)
//        );
//
//        return bucket.tryConsume(1);
//    }
//}



package com.hris.employee.security;

import com.hris.employee.entity.User;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRateLimiterService {

    private final ConcurrentHashMap<Long, Bucket> userBuckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
                5,
                Refill.greedy(5, Duration.ofSeconds(10))
        );
        return Bucket4j.builder().addLimit(limit).build();
    }

    public boolean tryConsume(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User or User ID cannot be null");
        }

        Bucket bucket = userBuckets.computeIfAbsent(
                user.getId(),
                id -> createNewBucket()
        );
        return bucket.tryConsume(1);
    }
}
