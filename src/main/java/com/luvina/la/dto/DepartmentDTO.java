package com.luvina.la.dto;

/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin phòng ban.
 * Bao gồm ID và tên của phòng ban.
 */
@Getter
@Setter
public class DepartmentDTO {

    /** ID duy nhất của phòng ban. */
    private Long departmentId;

    /** Tên của phòng ban. */
    private String departmentName;
}