/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeDTO.java, 10/05/2026 tdthang
 */
package com.luvina.la.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin nhân viên.
 * Bao gồm thông tin cá nhân, phòng ban và chứng chỉ tốt nhất của nhân viên.
 *
 * @author tdthang
 */
@Getter
@Setter
public class EmployeeDTO {

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
