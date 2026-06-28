package com.ems.service.impl;

import com.ems.dto.request.EmployeeRequest;
import com.ems.dto.response.EmployeeResponse;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.entity.EmployeeStatus;
import com.ems.exception.EmsExceptions.*;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Employee with email '" + request.getEmail() + "' already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "id", request.getDepartmentId()));

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .designation(request.getDesignation())
                .salary(request.getSalary())
                .dateOfJoining(request.getDateOfJoining())
                .status(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE)
                .department(department)
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Created employee with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> filterEmployees(String name, Long departmentId,
                                                   EmployeeStatus status, String designation,
                                                   BigDecimal minSalary, BigDecimal maxSalary,
                                                   Pageable pageable) {
        return employeeRepository.findWithFilters(
                name, departmentId, status, designation, minSalary, maxSalary, pageable
        ).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Check email uniqueness only if email changed
        if (!employee.getEmail().equals(request.getEmail()) &&
                employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already in use");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "id", request.getDepartmentId()));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setDateOfJoining(request.getDateOfJoining());
        employee.setStatus(request.getStatus());
        employee.setDepartment(department);

        Employee updated = employeeRepository.save(employee);
        log.info("Updated employee with id: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployeeStatus(Long id, EmployeeStatus status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setStatus(status);
        return mapToResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employeeRepository.delete(employee);
        log.info("Deleted employee with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department", "id", departmentId);
        }
        return employeeRepository.findByDepartmentId(departmentId, pageable)
                .map(this::mapToResponse);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFirstName() + " " + employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .designation(employee.getDesignation())
                .salary(employee.getSalary())
                .dateOfJoining(employee.getDateOfJoining())
                .status(employee.getStatus())
                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getName())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
