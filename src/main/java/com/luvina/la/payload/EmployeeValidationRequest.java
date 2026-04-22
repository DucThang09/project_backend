package com.luvina.la.payload;

import lombok.Data;

/**
 * Payload validate-only dùng chung cho add/edit employee.
 */
@Data
public class EmployeeValidationRequest {
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
