package com.ems.repository;

import com.ems.entity.Employee;
import com.ems.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    @Query("""
        SELECT e FROM Employee e
        WHERE (:name IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
                              OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:departmentId IS NULL OR e.department.id = :departmentId)
        AND (:status IS NULL OR e.status = :status)
        AND (:designation IS NULL OR LOWER(e.designation) LIKE LOWER(CONCAT('%', :designation, '%')))
        AND (:minSalary IS NULL OR e.salary >= :minSalary)
        AND (:maxSalary IS NULL OR e.salary <= :maxSalary)
    """)
    Page<Employee> findWithFilters(
            @Param("name") String name,
            @Param("departmentId") Long departmentId,
            @Param("status") EmployeeStatus status,
            @Param("designation") String designation,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            Pageable pageable
    );

    long countByDepartmentId(Long departmentId);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
    long countByStatus(@Param("status") EmployeeStatus status);
}
