package com.hris.employee.repository;


import com.hris.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>{
	

    Optional<Department> findByDeptId(Long deptId);
    Optional<Department> findByDeptName(String deptName);

}
