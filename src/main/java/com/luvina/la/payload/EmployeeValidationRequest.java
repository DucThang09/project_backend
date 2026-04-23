package com.luvina.la.payload;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import lombok.Data;

/**
 * Payload dùng chung cho validate, thêm mới và cập nhật nhân viên.
 */
@Data
public class EmployeeValidationRequest {
    private String employeeId;
    private String employeeLoginId;
    private String departmentId;
    private String employeeName;
    private String employeeNameKana;
    private String employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;
    private String employeeLoginPassword;
    private String employeeLoginPasswordConfirm;
    private String certificationId;
    private String certificationStartDate;
    private String certificationEndDate;
    private String score;
}
