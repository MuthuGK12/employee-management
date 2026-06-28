package com.ems.config;

import com.ems.entity.Department;
import com.ems.entity.Role;
import com.ems.entity.User;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedDepartments();
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@ems.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(Role.ROLE_ADMIN))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created — username: admin / password: admin123");
        }

        if (!userRepository.existsByUsername("manager")) {
            User manager = User.builder()
                    .username("manager")
                    .email("manager@ems.com")
                    .password(passwordEncoder.encode("manager123"))
                    .roles(Set.of(Role.ROLE_MANAGER))
                    .enabled(true)
                    .build();
            userRepository.save(manager);
            log.info("Manager user created — username: manager / password: manager123");
        }
    }

    private void seedDepartments() {
        if (departmentRepository.count() == 0) {
            List<Department> departments = List.of(
                    Department.builder().name("Engineering").description("Software development").location("Chennai").build(),
                    Department.builder().name("Human Resources").description("HR and talent").location("Bangalore").build(),
                    Department.builder().name("Finance").description("Accounts and budgeting").location("Mumbai").build(),
                    Department.builder().name("Operations").description("Operational support").location("Hyderabad").build()
            );
            departmentRepository.saveAll(departments);
            log.info("Sample departments seeded");
        }
    }
}
