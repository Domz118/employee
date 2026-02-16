package com.hris.employee.service;

import com.hris.employee.dto.EmployeeDTO;
import com.hris.employee.entity.Department;
import com.hris.employee.entity.Employee;
import com.hris.employee.repository.DepartmentRepository;
import com.hris.employee.repository.EmployeeRepository;
import com.hris.employee.service.interfce.EmployeeService;
import com.hris.employee.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


    @Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

        private final EmployeeRepository employeeRepository;
        private final DepartmentRepository departmentRepository;

        // LIST ALL
        @Override
        public List<EmployeeDTO> getAll() {

            return employeeRepository.findAll()
                    .stream()
                    .map(EmployeeDTO::new)
                    .toList();
        }

        // PAGE ALL
        @Override
        public Page<EmployeeDTO> getAllWithPage(int page, int size, String sortBy) {
          Page<Employee> employees =   employeeRepository.findAll(
                    org.springframework.data.domain.PageRequest.of(
                            page,
                            size,
                            org.springframework.data.domain.Sort.by(sortBy)
                    )
            );
         return  employees.map(EmployeeDTO::new);

        }


        @Override
        public EmployeeDTO getById(Long empId) throws Exception {

            Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() ->
                            new Exception("ID NOT FOUND :::: " + empId));

            return new EmployeeDTO(employee);
        }


        // PAGE BY DEPARTMENT
        @Override
        public Page<EmployeeDTO> getByDepartment(Long deptId, Pageable pageable) {

            Page<Employee> employees =
                    employeeRepository.findByDepartment_DeptId(deptId, pageable);

            return employees.map(EmployeeDTO::new);
        }


        @CacheEvict(value = {"employees", "employeesPage", "employeesByDept"}, allEntries = true)
        @Override
        public EmployeeDTO save(EmployeeDTO dto) {

            Department department = departmentRepository
                    .findById(dto.getDeptId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            Employee employee = Employee.builder()
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .empName(dto.getEmpName())
                    .empMobileNo(dto.getEmpMobileNo())
                    .empAdd(dto.getEmpAdd())
                    .department(department)
                    .build();

            Employee saved = employeeRepository.save(employee);

            return Mappers.convertEmployeeToDTO(saved);
        }




        // ✅ UPDATE
        @CacheEvict(value = {"employees", "employeesPage", "employeesByDept"}, allEntries = true)
        @Override
        public EmployeeDTO update(EmployeeDTO dto, Long id) {

            Employee existing = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            existing.setFirstName(dto.getFirstName());
            existing.setLastName(dto.getLastName());
            existing.setEmpName(dto.getEmpName());
            existing.setEmpMobileNo(dto.getEmpMobileNo());
            existing.setEmpAdd(dto.getEmpAdd());

            if (dto.getDeptId() != null) {
                Department department = departmentRepository.findById(dto.getDeptId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                existing.setDepartment(department);
            }

            Employee updated = employeeRepository.save(existing);

            return Mappers.convertEmployeeToDTO(updated);
        }


        // DELETE
        @CacheEvict(value = {"employees", "employeesPage", "employeesByDept"}, allEntries = true)
        @Override
        public Map<String, Boolean> delete(Long empId) {
            employeeRepository.deleteById(empId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("Delete", Boolean.TRUE);
            return response;
        }
    }


