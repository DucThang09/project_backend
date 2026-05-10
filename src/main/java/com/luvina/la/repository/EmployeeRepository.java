package com.luvina.la.repository;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.entity.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository truy cập dữ liệu nhân viên.
 * Cung cấp các truy vấn phục vụ validate login, đếm tổng số bản ghi và lấy danh sách nhân viên.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Tìm nhân viên theo login ID.
     *
     * @param employeeLoginId login ID của nhân viên
     * @return Optional chứa Employee nếu tìm thấy, ngược lại trả về rỗng
     */
    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    /**
     * Đếm tổng số nhân viên theo điều kiện lọc.
     * Loại trừ tài khoản admin khỏi kết quả.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm kiếm
     * @return tổng số nhân viên phù hợp
     */
    @Query(value = """
            SELECT COUNT(e.employee_id)
            FROM employees e
            INNER JOIN departments d ON e.department_id = d.department_id
            WHERE 1 = 1
              AND e.employee_role <> 1
              AND (:departmentId IS NULL OR e.department_id = :departmentId)
              AND (:employeeName IS NULL OR :employeeName = ''
                   OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '\\\\')
            """, nativeQuery = true)
    Long countTotalEmployees(@Param("departmentId") Long departmentId,
                             @Param("employeeName") String employeeName);

    /**
     * Lấy danh sách nhân viên với điều kiện lọc, sắp xếp và phân trang.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm kiếm
     * @param ordEmployeeName thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate thứ tự sắp xếp theo ngày hết hạn
     * @param limit số lượng bản ghi tối đa
     * @param offset vị trí bắt đầu
     * @return danh sách dòng dữ liệu thô từ truy vấn native
     */
    @Query(value = """
            SELECT
               e.employee_id, e.employee_name, e.employee_birth_date,
               d.department_name, e.employee_email, e.employee_telephone,
               c.certification_name, ec.end_date, ec.score
           FROM employees e
           INNER JOIN departments d ON e.department_id = d.department_id
           LEFT JOIN employees_certifications ec ON e.employee_id = ec.employee_id
           LEFT JOIN certifications c ON ec.certification_id = c.certification_id
           WHERE 1 = 1
             AND e.employee_role <> 1
             AND (:departmentId IS NULL OR e.department_id = :departmentId)
              AND (:employeeName IS NULL OR :employeeName = ''
                   OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '\\\\')
           ORDER BY
             CASE WHEN :ordEmployeeName = 'ASC'  THEN e.employee_name END ASC,
             CASE WHEN :ordEmployeeName = 'DESC' THEN e.employee_name END DESC,
             CASE WHEN :ordCertificationName = 'ASC'  THEN c.certification_level END ASC,
             CASE WHEN :ordCertificationName = 'DESC' THEN c.certification_level END DESC,
             CASE WHEN :ordEndDate = 'ASC'  THEN ec.end_date END ASC,
             CASE WHEN :ordEndDate = 'DESC' THEN ec.end_date END DESC,
             e.employee_id ASC
           LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Object[]> findEmployees(
            @Param("departmentId") Long departmentId,
            @Param("employeeName") String employeeName,
            @Param("ordEmployeeName") String ordEmployeeName,
            @Param("ordCertificationName") String ordCertificationName,
            @Param("ordEndDate") String ordEndDate,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset
    );

    /**
     * Lấy thông tin chi tiết của một nhân viên.
     *
     * @param employeeId ID nhân viên cần lấy chi tiết
     * @return dữ liệu thô từ truy vấn native
     */
    @Query(value = """
            SELECT
                e.employee_id,
                e.employee_login_id,
                d.department_id,
                d.department_name,
                e.employee_name,
                e.employee_name_kana,
                e.employee_birth_date,
                e.employee_email,
                e.employee_telephone,
                c.certification_id,
                c.certification_name,
                ec.start_date,
                ec.end_date,
                ec.score
            FROM employees e
            INNER JOIN departments d ON e.department_id = d.department_id
            LEFT JOIN employees_certifications ec ON e.employee_id = ec.employee_id
            LEFT JOIN certifications c ON ec.certification_id = c.certification_id
            WHERE e.employee_id = :employeeId
              AND e.employee_role <> 1
            """, nativeQuery = true)
    List<Object[]> findEmployeeDetail(@Param("employeeId") Long employeeId);
}
