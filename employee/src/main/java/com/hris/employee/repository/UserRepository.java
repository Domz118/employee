package com.hris.employee.repository;
import java.util.Optional;


import com.hris.employee.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    //TODO Strict version: throws if not found
    default User getByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
    Boolean existsByEmail(String email);
	Boolean existsByUsername(String username);
    @Query("select u from User u where u.username like :kw  order by u.username asc")
    Page<User> search(@Param("kw") String keyword, Pageable pageable);

    @Query("""
    SELECT u FROM User u
    WHERE UPPER(u.username) LIKE UPPER(CONCAT('%', :username, '%'))
""")
    <T> Page<T> searchUsersByUsername(@Param("username") String name,
                                      Pageable pageable,
                                      Class<T> type);

    @Query("SELECT user FROM User user WHERE user.email = :email")
    <T> Optional<T> getUserByEmail(@Param("email") String email, Class<T> type);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> getUserPasswordById(@Param("userId") Long userId);



}
