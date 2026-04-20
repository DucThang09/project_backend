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
}