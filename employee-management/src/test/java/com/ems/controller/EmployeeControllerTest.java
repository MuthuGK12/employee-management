package com.ems.controller;

import com.ems.dto.request.EmployeeRequest;
import com.ems.dto.response.EmployeeResponse;
import com.ems.entity.EmployeeStatus;
import com.ems.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@DisplayName("EmployeeController Integration Tests")
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private EmployeeService employeeService;

    private EmployeeResponse sampleResponse;
    private EmployeeRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = EmployeeResponse.builder()
                .id(1L)
                .firstName("Muthu")
                .lastName("GK")
                .fullName("Muthu GK")
                .email("muthu@ems.com")
                .designation("Java Developer")
                .salary(new BigDecimal("75000.00"))
                .dateOfJoining(LocalDate.of(2023, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .departmentId(1L)
                .departmentName("Engineering")
                .build();

        sampleRequest = new EmployeeRequest();
        sampleRequest.setFirstName("Muthu");
        sampleRequest.setLastName("GK");
        sampleRequest.setEmail("muthu@ems.com");
        sampleRequest.setDesignation("Java Developer");
        sampleRequest.setSalary(new BigDecimal("75000.00"));
        sampleRequest.setDateOfJoining(LocalDate.of(2023, 1, 15));
        sampleRequest.setDepartmentId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/employees — should create employee and return 201")
    void createEmployee_returns201() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("muthu@ems.com"))
                .andExpect(jsonPath("$.data.fullName").value("Muthu GK"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("GET /api/employees/{id} — should return employee")
    void getEmployee_returns200() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.designation").value("Java Developer"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("GET /api/employees — should return paginated list")
    void getAllEmployees_returns200() throws Exception {
        Page<EmployeeResponse> page = new PageImpl<>(List.of(sampleResponse));
        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].email").value("muthu@ems.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/employees/{id} — should delete and return 200")
    void deleteEmployee_returns200() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/employees — should return 400 on validation failure")
    void createEmployee_invalidRequest_returns400() throws Exception {
        EmployeeRequest invalid = new EmployeeRequest();
        // firstName, email, salary, departmentId are all missing

        mockMvc.perform(post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("GET /api/employees — should return 401 when not authenticated")
    void getEmployees_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }
}
