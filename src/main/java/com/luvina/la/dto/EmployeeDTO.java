package com.luvina.la.dto;

/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeDTO.java, April 13, 2026 tdthang
 */
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/** DTO chứa thông tin nhân viên. */
@Getter
@Setter
public class EmployeeDTO {

    private Long employeeId;
    private String employeeName;
    private LocalDate employeeBirthDate;
    private String departmentName;
    private String employeeEmail;
    private String employeeTelephone;
    private String certificationName;
    private LocalDate endDate;
    private BigDecimal score;
}