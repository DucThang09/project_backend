package com.luvina.la.repository;

import com.luvina.la.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository để truy cập dữ liệu nhân viên.
 * Cung cấp các phương thức truy vấn cơ bản và tùy chỉnh cho bảng employees.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Tìm nhân viên theo ID đăng nhập.
     *
     * @param employeeLoginId ID đăng nhập của nhân viên
     * @return Optional chứa Employee nếu tìm thấy, empty nếu không
     */
    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    /**
     * Đếm tổng số nhân viên theo điều kiện lọc.
     * Loại trừ tài khoản admin khỏi kết quả.
     *
     * @param departmentId ID phòng ban để lọc (có thể null)
     * @param employeeName Tên nhân viên để tìm kiếm (có thể null)
     * @return Tổng số nhân viên phù hợp
     */
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
