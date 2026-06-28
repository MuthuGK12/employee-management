package com.ems.service;

import com.ems.dto.request.EmployeeRequest;
import com.ems.dto.response.EmployeeResponse;
import com.ems.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    Page<EmployeeResponse> getAllEmployees(Pageable pageable);
    Page<EmployeeResponse> filterEmployees(String name, Long departmentId,
                                           EmployeeStatus status, String designation,
                                           BigDecimal minSalary, BigDecimal maxSalary,
                                           Pageable pageable);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    EmployeeResponse updateEmployeeStatus(Long id, EmployeeStatus status);
    void deleteEmployee(Long id);
    Page<EmployeeResponse> getEmployeesByDepartment(Long departmentId, Pageable pageable);
}
