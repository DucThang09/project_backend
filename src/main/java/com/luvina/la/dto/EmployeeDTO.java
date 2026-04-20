package com.luvina.la.dto;

/**
 * Data Transfer Object cho thông tin nhân viên.
 * Chứa dữ liệu nhân viên được trả về từ service để hiển thị trên giao diện.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin nhân viên.
 * Bao gồm thông tin cá nhân, phòng ban và chứng chỉ tốt nhất của nhân viên.
 */
@Getter
@Setter
public class EmployeeDTO {

    /** ID duy nhất của nhân viên. */
    private Long employeeId;

    /** Tên đầy đủ của nhân viên. */
    private String employeeName;

    /** Ngày sinh của nhân viên. */
    private LocalDate employeeBirthDate;

    /** Tên phòng ban mà nhân viên thuộc về. */
    private String departmentName;

    /** Địa chỉ email của nhân viên. */
    private String employeeEmail;

    /** Số điện thoại của nhân viên. */
    private String employeeTelephone;

    /** Tên chứng chỉ tốt nhất của nhân viên. */
    private String certificationName;

    /** Ngày kết thúc hiệu lực của chứng chỉ. */
    private LocalDate endDate;

    /** Điểm số đạt được trong kỳ thi chứng chỉ. */
    private BigDecimal score;

    /**
     * Tạo DTO từ một dòng kết quả truy vấn native.
     *
     * @param row mảng Object chứa dữ liệu của một nhân viên
     * @return EmployeeDTO đã được map
     */
    public static EmployeeDTO fromRow(Object[] row) {
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
     * Chuyển Object thành Long.
     *
     * @param value giá trị cần chuyển đổi
     * @return giá trị Long hoặc null
     */
    private static Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    /**
     * Chuyển Object thành String.
     *
     * @param value giá trị cần chuyển đổi
     * @return giá trị String hoặc null
     */
    private static String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    /**
     * Chuyển Object thành LocalDate.
     *
     * @param value giá trị cần chuyển đổi
     * @return LocalDate hoặc null
     */
    private static LocalDate toLocalDate(Object value) {
        return (value instanceof Date date) ? date.toLocalDate() : null;
    }

    /**
     * Chuyển Object thành BigDecimal.
     *
     * @param value giá trị cần chuyển đổi
     * @return giá trị BigDecimal hoặc null
     */
    private static BigDecimal toBigDecimal(Object value) {
        return value == null ? null : (BigDecimal) value;
    }
}
