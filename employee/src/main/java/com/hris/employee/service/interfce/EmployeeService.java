package com.hris.employee.service.interfce;

import com.hris.employee.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
     EmployeeDTO save(EmployeeDTO employee);
    EmployeeDTO update(EmployeeDTO employee, Long empId) throws Exception;
    EmployeeDTO getById(Long empId) throws Exception;
     List<EmployeeDTO> getAll();
     Map<String, Boolean> delete(Long empId) throws Exception;
   Page<EmployeeDTO> getAllWithPage(int page, int size, String sortBy);
   Page<EmployeeDTO> getByDepartment(Long deptId, Pageable pageable);
}
