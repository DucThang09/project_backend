/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * DepartmentDTO.java, 10/05/2026 tdthang
 */
package com.luvina.la.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin phòng ban.
 * Bao gồm ID và tên của phòng ban.
 * @author tdthang
 */
@Getter
@Setter
public class DepartmentDTO {

    private Long departmentId;
    private String departmentName;
}
