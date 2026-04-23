package com.luvina.la.dto;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin chứng chỉ.
 */
@Getter
@Setter
public class CertificationDTO {

    /** ID duy nhất của chứng chỉ. */
    private Long certificationId;

    /** Tên chứng chỉ. */
    private String certificationName;
}
