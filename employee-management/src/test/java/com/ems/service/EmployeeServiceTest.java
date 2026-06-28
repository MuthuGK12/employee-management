package com.ems.service;

import com.ems.dto.request.EmployeeRequest;
import com.ems.dto.response.EmployeeResponse;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.entity.EmployeeStatus;
import com.ems.exception.EmsExceptions.DuplicateResourceException;
import com.ems.exception.EmsExceptions.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private DepartmentRepository departmentRepository;

    @InjectMocks private EmployeeServiceImpl employeeService;

    private Department department;
    private Employee employee;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Engineering")
                .location("Chennai")
                .build();

        employee = Employee.builder()
                .id(1L)
                .firstName("Muthu")
                .lastName("GK")
                .email("muthu@ems.com")
                .phoneNumber("9876543210")
                .designation("Java Developer")
                .salary(new BigDecimal("75000.00"))
                .dateOfJoining(LocalDate.of(2023, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .department(department)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("Muthu");
        employeeRequest.setLastName("GK");
        employeeRequest.setEmail("muthu@ems.com");
        employeeRequest.setPhoneNumber("9876543210");
        employeeRequest.setDesignation("Java Developer");
        employeeRequest.setSalary(new BigDecimal("75000.00"));
        employeeRequest.setDateOfJoining(LocalDate.of(2023, 1, 15));
        employeeRequest.setStatus(EmployeeStatus.ACTIVE);
        employeeRequest.setDepartmentId(1L);
    }

    // --- Create ---

    @Test
    @DisplayName("Should create employee successfully")
    void createEmployee_success() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.createEmployee(employeeRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("muthu@ems.com");
        assertThat(response.getFullName()).isEqualTo("Muthu GK");
        assertThat(response.getDepartmentName()).isEqualTo("Engineering");

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void createEmployee_duplicateEmail_throwsException() {
        when(employeeRepository.existsByEmail("muthu@ems.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(employeeRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("muthu@ems.com");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found")
    void createEmployee_departmentNotFound_throwsException() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.createEmployee(employeeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department");
    }

    // --- Get by ID ---

    @Test
    @DisplayName("Should return employee by ID")
    void getEmployeeById_found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDesignation()).isEqualTo("Java Developer");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee not found by ID")
    void getEmployeeById_notFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee")
                .hasMessageContaining("99");
    }

    // --- Get All (Paginated) ---

    @Test
    @DisplayName("Should return paginated employees")
    void getAllEmployees_returnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeRepository.findAll(pageable)).thenReturn(page);

        Page<EmployeeResponse> result = employeeService.getAllEmployees(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("muthu@ems.com");
    }

    // --- Update ---

    @Test
    @DisplayName("Should update employee successfully")
    void updateEmployee_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeRequest.setSalary(new BigDecimal("90000.00"));
        EmployeeResponse response = employeeService.updateEmployee(1L, employeeRequest);

        assertThat(response).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
    }

    // --- Delete ---

    @Test
    @DisplayName("Should delete employee successfully")
    void deleteEmployee_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent employee")
    void deleteEmployee_notFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).delete(any());
    }

    // --- Status update ---

    @Test
    @DisplayName("Should update employee status")
    void updateEmployeeStatus_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.updateEmployeeStatus(1L, EmployeeStatus.ON_LEAVE);

        assertThat(response).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
    }
}
