package com.luvina.la.repository;

import com.luvina.la.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    @Query(value = """
            SELECT COUNT(e.employee_id)
            FROM employees e
            INNER JOIN departments d ON e.department_id = d.department_id
            WHERE 1 = 1
              -- Không tính tài khoản admin vào danh sách user.
              AND COALESCE(UPPER(TRIM(e.employee_role)), 'USER') <> 'ADMIN'
              AND (:departmentId IS NULL OR e.department_id = :departmentId)
              AND (:employeeName IS NULL OR TRIM(:employeeName) = ''
                   OR e.employee_name LIKE CONCAT('%', TRIM(:employeeName), '%'))
            """, nativeQuery = true)
    Long countTotalEmployees(@Param("departmentId") Long departmentId,
                             @Param("employeeName") String employeeName);
}
