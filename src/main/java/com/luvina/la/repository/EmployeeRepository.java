package com.luvina.la.repository;

import com.luvina.la.entity.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository d? truy c?p d? li?u nhân viên.
 * Cung c?p các phuong th?c truy v?n co b?n và truy v?n danh sách nhân viên cho b?ng employees.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Tìm nhân viên theo ID dang nh?p.
     *
     * @param employeeLoginId ID dang nh?p c?a nhân viên
     * @return Optional ch?a Employee n?u tìm th?y, empty n?u không
     */
    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    /**
     * Ð?m t?ng s? nhân viên theo di?u ki?n l?c.
     * Lo?i tr? tài kho?n admin kh?i k?t qu?.
     *
     * @param departmentId ID phòng ban d? l?c
     * @param employeeName Tên nhân viên d? tìm ki?m
     * @return T?ng s? nhân viên phù h?p
     */
    @Query(value = """
            SELECT COUNT(e.employee_id)
            FROM employees e
            INNER JOIN departments d ON e.department_id = d.department_id
            WHERE 1 = 1
              AND COALESCE(UPPER(TRIM(e.employee_role)), 'USER') <> 'ADMIN'
              AND (:departmentId IS NULL OR e.department_id = :departmentId)
              AND (:employeeName IS NULL OR TRIM(:employeeName) = ''
                   OR e.employee_name LIKE CONCAT('%', TRIM(:employeeName), '%'))
            """, nativeQuery = true)
    Long countTotalEmployees(@Param("departmentId") Long departmentId,
                             @Param("employeeName") String employeeName);

    /**
     * L?y danh sách nhân viên v?i di?u ki?n l?c, s?p x?p và phân trang.
     *
     * @param departmentId ID phòng ban d? l?c
     * @param employeeName Tên nhân viên d? tìm ki?m
     * @param ordEmployeeName Th? t? s?p x?p theo tên nhân viên
     * @param ordCertificationName Th? t? s?p x?p theo tên ch?ng ch?
     * @param ordEndDate Th? t? s?p x?p theo ngày h?t h?n
     * @param limit S? lu?ng b?n ghi t?i da
     * @param offset V? trí b?t d?u
     * @return Danh sách dòng d? li?u thô t? truy v?n native
     */
    @Query(value = """
            SELECT
                e.employee_id, e.employee_name, e.employee_birth_date,
                d.department_name, e.employee_email, e.employee_telephone,
                c.certification_name, ec.end_date, ec.score
            FROM employees e
            INNER JOIN departments d ON e.department_id = d.department_id
            LEFT JOIN (
                SELECT ec1.employee_id, ec1.certification_id, ec1.end_date,
                       ec1.score, ec1.employee_certification_id
                FROM employee_certifications ec1
                INNER JOIN certifications c1 ON ec1.certification_id = c1.certification_id
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM employee_certifications ec2
                    INNER JOIN certifications c2 ON ec2.certification_id = c2.certification_id
                    WHERE ec2.employee_id = ec1.employee_id
                      AND (c2.certification_level > c1.certification_level
                           OR (c2.certification_level = c1.certification_level AND ec2.end_date > ec1.end_date)
                           OR (c2.certification_level = c1.certification_level
                               AND ec2.end_date = ec1.end_date
                               AND ec2.employee_certification_id > ec1.employee_certification_id))
                )
            ) ec ON e.employee_id = ec.employee_id
            LEFT JOIN certifications c ON ec.certification_id = c.certification_id
            WHERE 1 = 1
              AND COALESCE(UPPER(TRIM(e.employee_role)), 'USER') <> 'ADMIN'
              AND (:departmentId IS NULL OR e.department_id = :departmentId)
              AND (:employeeName IS NULL OR TRIM(:employeeName) = ''
                   OR e.employee_name LIKE CONCAT('%', TRIM(:employeeName), '%'))
            ORDER BY
              CASE WHEN UPPER(TRIM(COALESCE(:ordEmployeeName, ''))) = 'ASC' THEN e.employee_name END ASC,
              CASE WHEN UPPER(TRIM(COALESCE(:ordEmployeeName, ''))) = 'DESC' THEN e.employee_name END DESC,
              CASE WHEN UPPER(TRIM(COALESCE(:ordCertificationName, ''))) = 'ASC' THEN c.certification_name END ASC,
              CASE WHEN UPPER(TRIM(COALESCE(:ordCertificationName, ''))) = 'DESC' THEN c.certification_name END DESC,
              CASE WHEN UPPER(TRIM(COALESCE(:ordEndDate, ''))) = 'ASC' THEN ec.end_date END ASC,
              CASE WHEN UPPER(TRIM(COALESCE(:ordEndDate, ''))) = 'DESC' THEN ec.end_date END DESC,
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
}
