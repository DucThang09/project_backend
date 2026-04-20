package com.luvina.la.service.impl;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Service;

/**
 * Implementation của EmployeeService.
 * Xử lý logic nghiệp vụ liên quan đến nhân viên sử dụng truy vấn SQL native
 * với sắp xếp động và phân trang.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    /** Repository để truy cập dữ liệu nhân viên. */
    private final EmployeeRepository employeeRepository;

    /** EntityManager để thực hiện truy vấn SQL native. */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor để inject dependencies.
     *
     * @param employeeRepository Repository cho nhân viên
     */
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getTotalEmployees(Long departmentId, String employeeName) {
        return employeeRepository.countTotalEmployees(departmentId, employeeName);
    }

    /**
     * {@inheritDoc}
     * Triển khai sắp xếp động theo nhiều tiêu chí với ưu tiên.
     */
    @Override
    public List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit) {

        int finalLimit = (limit == null || limit <= 0) ? 20 : limit;
        int finalOffset = (offset == null || offset < 0) ? 0 : offset;

        String orderBy = buildOrderByClause(ordEmployeeName, ordCertificationName, ordEndDate);
        List<Object[]> results = getEmployeesWithDynamicSort(
                departmentId,
                employeeName,
                finalLimit,
                finalOffset,
                orderBy
        );

        List<EmployeeDTO> dtos = new ArrayList<>(results.size());
        for (Object[] row : results) {
            dtos.add(mapToEmployeeDto(row));
        }
        return dtos;
    }

    /**
     * Thực hiện truy vấn SQL native để lấy danh sách nhân viên với sắp xếp động.
     * Sử dụng subquery để lấy chứng chỉ "tốt nhất" cho mỗi nhân viên.
     *
     * @param departmentId ID phòng ban để lọc
     * @param employeeName Tên nhân viên để tìm kiếm
     * @param limit Số lượng bản ghi tối đa
     * @param offset Vị trí bắt đầu
     * @param orderBy Mệnh đề ORDER BY động
     * @return Danh sách mảng Object chứa dữ liệu nhân viên
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> getEmployeesWithDynamicSort(
            Long departmentId,
            String employeeName,
            Integer limit,
            Integer offset,
            String orderBy) {
        // Chỉ lấy chứng chỉ "cao nhất/gần nhất" cho mỗi nhân viên để hiển thị trên list.
        String sql = """
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
                order by e.employee_name,c.certification_name, ec.end_date
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("departmentId", departmentId);
        query.setParameter("employeeName", employeeName);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * Xây dựng mệnh đề ORDER BY động dựa trên các tham số sắp xếp.
     * Ưu tiên cột đang được sort lên đầu, sau đó là các cột khác theo thứ tự mặc định.
     *
     * @param ordEmployeeName Thứ tự sắp xếp tên nhân viên
     * @param ordCertificationName Thứ tự sắp xếp tên chứng chỉ
     * @param ordEndDate Thứ tự sắp xếp ngày kết thúc
     * @return Mệnh đề ORDER BY hoàn chỉnh
     */
    private String buildOrderByClause(String ordEmployeeName,
                                      String ordCertificationName,
                                      String ordEndDate) {
        List<SortField> sortFields = new ArrayList<>(Arrays.asList(
                new SortField("e.employee_name", ordEmployeeName),
                new SortField("c.certification_name", ordCertificationName),
                new SortField("ec.end_date", ordEndDate)
        ));

        int activeSortIndex = findActiveSortIndex(sortFields);
        // Lần đầu vào màn hình, spec yêu cầu sort mặc định theo employee_id tăng dần.
        if (activeSortIndex < 0) {
            return "\nORDER BY e.employee_name ASC";
        }

        // Khi người dùng sort 1 cột, đưa cột đó lên ưu tiên đầu tiên.
        if (activeSortIndex > 0) {
            SortField activeSort = sortFields.remove(activeSortIndex);
            sortFields.add(0, activeSort);
        }

        StringBuilder sb = new StringBuilder("\nORDER BY ");
        for (SortField field : sortFields) {
            String direction = hasText(field.order()) ? normalizeSort(field.order()) : "ASC";
            sb.append(field.column()).append(' ').append(direction).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    /**
     * Chuẩn hóa hướng sắp xếp về ASC hoặc DESC.
     *
     * @param sort Chuỗi sắp xếp đầu vào
     * @return "ASC" hoặc "DESC"
     */
    private String normalizeSort(String sort) {
        return "DESC".equalsIgnoreCase(sort) ? "DESC" : "ASC";
    }

    /**
     * Kiểm tra xem chuỗi có chứa text không (không null và không rỗng).
     *
     * @param value Chuỗi cần kiểm tra
     * @return true nếu có text, false nếu không
     */
    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Tìm chỉ số của trường sắp xếp đang active (có giá trị sort).
     *
     * @param sortFields Danh sách các trường sắp xếp
     * @return Chỉ số của trường đang active, -1 nếu không có
     */
    private int findActiveSortIndex(List<SortField> sortFields) {
        for (int i = 0; i < sortFields.size(); i++) {
            if (hasText(sortFields.get(i).order())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Chuyển đổi mảng Object từ kết quả truy vấn thành EmployeeDTO.
     *
     * @param row Mảng Object chứa dữ liệu một hàng
     * @return EmployeeDTO đã được map
     */
    private EmployeeDTO mapToEmployeeDto(Object[] row) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(toLong(row[0]));
        dto.setEmployeeName(toStringValue(row[1]));
        dto.setEmployeeBirthDate(toLocalDate(row[2]));
        dto.setDepartmentName(toStringValue(row[3]));
        dto.setEmployeeEmail(toStringValue(row[4]));
        dto.setEmployeeTelephone(toStringValue(row[5]));
        dto.setCertificationName(toStringValue(row[6]));
        dto.setEndDate(toLocalDate(row[7]));
        dto.setScore(toBigDecimal(row[8]));
        return dto;
    }

    /**
     * Chuyển đổi Object thành Long.
     *
     * @param value Giá trị cần chuyển đổi
     * @return Long value hoặc null
     */
    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    /**
     * Chuyển đổi Object thành String.
     *
     * @param value Giá trị cần chuyển đổi
     * @return String value hoặc null
     */
    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    /**
     * Chuyển đổi Object thành LocalDate.
     *
     * @param value Giá trị cần chuyển đổi (Date SQL)
     * @return LocalDate hoặc null
     */
    private LocalDate toLocalDate(Object value) {
        return (value instanceof Date date) ? date.toLocalDate() : null;
    }

    /**
     * Chuyển đổi Object thành BigDecimal.
     *
     * @param value Giá trị cần chuyển đổi
     * @return BigDecimal value
     */
    private BigDecimal toBigDecimal(Object value) {
        return (BigDecimal) value;
    }

    /**
     * Record để lưu trữ thông tin trường sắp xếp.
     *
     * @param column Tên cột trong SQL
     * @param order Hướng sắp xếp (ASC/DESC)
     */
    private record SortField(String column, String order) {
    }
}
