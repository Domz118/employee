package com.hris.employee.dto;

import com.hris.employee.entity.Employee;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {

    private Long empId;
    private String firstName;
    private String lastName;
    private String empName;
    private Long empMobileNo;
    private String empAdd;
    private Long deptId;

    public EmployeeDTO(Employee emp) {
        this.empId = emp.getEmpId();
        this.firstName = emp.getFirstName();
        this.lastName = emp.getLastName();
        this.empName = emp.getEmpName();
        this.empMobileNo = emp.getEmpMobileNo();
        this.empAdd = emp.getEmpAdd();
        this.deptId = emp.getDepartment() != null
                ? emp.getDepartment().getDeptId()
                : null;
    }
}
