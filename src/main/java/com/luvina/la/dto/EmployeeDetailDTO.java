/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeDetailDTO.java, 10/05/2026 tdthang
 */
package com.luvina.la.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin chi tiết của nhân viên.
 *
 * @author tdthang
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
}
