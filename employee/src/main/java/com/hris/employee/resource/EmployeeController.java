package com.hris.employee.resource;


import java.util.List;
import java.util.Map;

import com.hris.employee.dto.EmployeeDTO;
import com.hris.employee.service.interfce.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value="/api")
public class EmployeeController {
    @Autowired
    private final EmployeeService employeeService;


    @PostMapping(value="/employee",produces = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody EmployeeDTO employee) {

        EmployeeDTO empDTO = employeeService.save(employee);
        return ResponseEntity.ok().body(empDTO);
    }

    @PutMapping(value="/employee/{id}",produces = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<EmployeeDTO> updateEmployee(@RequestBody EmployeeDTO employee
            ,  @PathVariable("id") Long empId) throws Exception {

        EmployeeDTO empDTO = employeeService.update(employee, empId);
        return ResponseEntity.ok().body(empDTO);
    }

    @DeleteMapping(value="/employee/{id}")
    public Map<String, Boolean> deleteEmployee(@PathVariable long id) throws Exception{
        return employeeService.delete(id);
    }

    @GetMapping(value="/employee/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<EmployeeDTO> findById(@PathVariable Long id) throws Exception {

        EmployeeDTO empDTO = employeeService.getById(id);
        return ResponseEntity.ok().body(empDTO);
    }

    @GetMapping(value="/employee")
    public ResponseEntity<List<EmployeeDTO>> listAll()
    {
        List<EmployeeDTO> empDTO = employeeService.getAll();
        return ResponseEntity.ok().body(empDTO);
    }


        //  GET ALL WITH PAGINATION
        @GetMapping
        public Page<EmployeeDTO> getAllWithPage(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "5") int size,
                @RequestParam(defaultValue = "firstName") String sortBy
        ) {
            return employeeService.getAllWithPage(page, size, sortBy);
        }



    // ✅ GET BY DEPARTMENT WITH PAGE
        @GetMapping("/department/{deptId}")
        public Page<EmployeeDTO> getByDepartment(
                @PathVariable Long deptId,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "5") int size,
                @RequestParam(defaultValue = "firstName") String sortBy
        ) {

            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(sortBy)
            );

            return employeeService.getByDepartment(deptId, pageable);
        }
    }




