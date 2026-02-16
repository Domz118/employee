package com.hris.employee.repository;

import com.hris.employee.entity.Employee;
import com.hris.employee.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmpId(Long empId);
    // Safe derived query method for mobile number
    Optional<Employee> findByEmpMobileNo(Long empMobileNo);
    Optional<Employee> findByFirstNameAndLastName(String firstname,String lastname);
    // Strict version: throws if not found
    default Employee getByFirstnameAndLastname(String firstname,String lastname) {
        return findByFirstNameAndLastName(firstname,lastname)
                .orElseThrow(() -> new RuntimeException("User not found: " + firstname + lastname));
    }
    Page<Employee> findByDepartment_DeptId(Long deptId, Pageable pageable);

}
