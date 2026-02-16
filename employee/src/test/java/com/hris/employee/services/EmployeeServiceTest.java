package com.hris.employee.services;

import com.hris.employee.dto.EmployeeDTO;
import com.hris.employee.entity.Department;
import com.hris.employee.entity.Employee;
import com.hris.employee.repository.DepartmentRepository;
import com.hris.employee.repository.EmployeeRepository;
import com.hris.employee.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    //DTO CONVERT TO MAPPER
    private Employee employee;
    //DTO CONVERT TO MAPPER
    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .deptId(3L)
                .deptName("SUPER_DEPT")
                .build();

        employee = Employee.builder()
                .empId(3L)
                .firstName("Domz")
                .lastName("eleven")
                .empName("Domz eleven")
                .empMobileNo(1234887610L)
                .empAdd("Street")
                .department(department)
                .build();
    }

    //  PAGE TEST
    @Test
    void testGetEmployeesByDepartment_WithPage() {

        Pageable pageable = PageRequest.of(0, 5, Sort.by("firstName"));
        List<Employee> employeeList = List.of(employee);

        Page<Employee> page =
                new PageImpl<>(employeeList, pageable, employeeList.size());

        when(employeeRepository.findByDepartment_DeptId(3L, pageable))
                .thenReturn(page);

        Page<EmployeeDTO> result =
                employeeService.getByDepartment(3L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Domz", result.getContent().get(0).getFirstName());
        assertEquals(3L, result.getContent().get(0).getDeptId());

        verify(employeeRepository)
                .findByDepartment_DeptId(3L, pageable);

            }

    // SAVE TEST
    @Test
    void testSaveEmployee() {

        EmployeeDTO dto = new EmployeeDTO();
        dto.setFirstName("Domz");
        dto.setLastName("eleven");
        dto.setEmpName("Domz eleven");
        dto.setEmpMobileNo(1234887610L);
        dto.setEmpAdd("Street");
        dto.setDeptId(3L);

        when(departmentRepository.findById(3L))
                .thenReturn(Optional.of(department));

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(employee);

        EmployeeDTO saved = employeeService.save(dto);

        assertNotNull(saved);
        assertEquals("Domz", saved.getFirstName());
        assertEquals(3L, saved.getDeptId());

        verify(employeeRepository).save(any(Employee.class));
    }


    //  UPDATE TEST
    @Test
    void testUpdateEmployee() {

        EmployeeDTO dto = new EmployeeDTO();
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setEmpName("Updated Name");
        dto.setEmpMobileNo(999999999L);
        dto.setEmpAdd("New Street");
        dto.setDeptId(3L);

        when(employeeRepository.findById(3L))
                .thenReturn(Optional.of(employee));

        when(departmentRepository.findById(3L))
                .thenReturn(Optional.of(department));

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(employee);

        EmployeeDTO updated =
                employeeService.update(dto, 3L);

        assertNotNull(updated);
        verify(employeeRepository).save(any(Employee.class));
    }

}
