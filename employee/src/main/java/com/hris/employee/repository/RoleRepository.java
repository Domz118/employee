package com.hris.employee.repository;

import java.util.Optional;

import com.hris.employee.entity.Role;
import com.hris.employee.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
    Boolean existsBy();

}
