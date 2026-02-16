package com.hris.employee.utils;

import com.hris.employee.dto.DepartmentDTO;
import com.hris.employee.dto.EmployeeDTO;
import com.hris.employee.dto.response.PageResponse;
import com.hris.employee.dto.response.UserResponse;

import com.hris.employee.entity.Department;
import com.hris.employee.entity.Employee;
import com.hris.employee.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import java.util.List;


public class Mappers {

    private Mappers() {
        super();
    }

    public static @NotNull UserResponse fromUser(final @NotNull User user) {
        final UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name()) // ERole -> String
                .toList();
        response.setRoles(roles);
        return response;
    }
    public static EmployeeDTO convertEmployeeToDTO(Employee emp) {
        return new EmployeeDTO(emp);
    }

    public static @NotNull Employee fromEmployee(final @NotNull EmployeeDTO employee) {
        final Employee empDTO = new Employee();
        employee.setEmpId(empDTO.getEmpId());
        employee.setEmpName(empDTO.getEmpName());
        employee.setEmpAdd(empDTO.getEmpAdd());
        employee.setEmpMobileNo(empDTO.getEmpMobileNo());
        employee.setDeptId(empDTO.getDepartment().getDeptId());
        return empDTO;
    }

    public static DepartmentDTO convertDepartmentToDTO(Department department) {
        return new DepartmentDTO(department);
    }

    public static @NotNull Department fromDepartment(final @NotNull DepartmentDTO departmentDTO) {
        final Department department = new Department();
        department.setDeptId(departmentDTO.getDeptId());
        department.setDeptName(departmentDTO.getDeptName());
        return department;
    }


    public static @NotNull PageResponse<UserResponse> fromPageOfUsers(final @NotNull Page<User> userPage) {
        final PageResponse<UserResponse> response = new PageResponse<>();
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());
        response.setNumber(userPage.getNumber());
        response.setSize(userPage.getSize());
        response.setNumberOfElements(userPage.getNumberOfElements());
        response.setHasContent(userPage.hasContent());
        response.setHasPrevious(userPage.hasPrevious());
        response.setHasNext(userPage.hasNext());
        response.setLast(userPage.isLast());
        response.setFirst(userPage.isFirst());
        response.setContent(fromListOfUsers(userPage.getContent()));

        return response;
    }


    public static List<UserResponse> fromListOfUsers(final List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream().map(Mappers::fromUser).toList();
    }



}
