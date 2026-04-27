package com.luvina.la.dto;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO chua thong tin chi tiet cua nhan vien.
 */
@Getter
@Setter
public class EmployeeDetailDTO {

    private Long employeeId;
    private String employeeLoginId;
    private Long departmentId;
    private String departmentName;
    private String employeeName;
    private String employeeNameKana;
    private LocalDate employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;
    private Long certificationId;
    private String certificationName;
    private LocalDate certificationStartDate;
    private LocalDate certificationEndDate;
    private BigDecimal score;

    /**
     * Tao DTO tu mot dong ket qua native query.
     *
     * @param row du lieu truy van
     * @return DTO chi tiet nhan vien
     */
    public static EmployeeDetailDTO fromRow(Object[] row) {
        EmployeeDetailDTO dto = new EmployeeDetailDTO();
        dto.setEmployeeId(toLong(row[0]));
        dto.setEmployeeLoginId(toStringValue(row[1]));
        dto.setDepartmentId(toLong(row[2]));
        dto.setDepartmentName(toStringValue(row[3]));
        dto.setEmployeeName(toStringValue(row[4]));
        dto.setEmployeeNameKana(toStringValue(row[5]));
        dto.setEmployeeBirthDate(toLocalDate(row[6]));
        dto.setEmployeeEmail(toStringValue(row[7]));
        dto.setEmployeeTelephone(toStringValue(row[8]));
        dto.setCertificationId(toLong(row[9]));
        dto.setCertificationName(toStringValue(row[10]));
        dto.setCertificationStartDate(toLocalDate(row[11]));
        dto.setCertificationEndDate(toLocalDate(row[12]));
        dto.setScore(toBigDecimal(row[13]));
        return dto;
    }

    private static Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private static String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private static LocalDate toLocalDate(Object value) {
        return (value instanceof Date date) ? date.toLocalDate() : null;
    }

    private static BigDecimal toBigDecimal(Object value) {
        return value == null ? null : (BigDecimal) value;
    }
}
