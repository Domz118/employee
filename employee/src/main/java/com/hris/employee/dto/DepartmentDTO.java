package com.hris.employee.dto;

import com.hris.employee.entity.Department;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    private Long deptId;
    private String deptName;

    public DepartmentDTO(Department department) {
        this.deptId = department.getDeptId();
        this.deptName = department.getDeptName();
    }
}