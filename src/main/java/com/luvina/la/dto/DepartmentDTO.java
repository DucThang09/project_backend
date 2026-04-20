package com.luvina.la.dto;

/**
 * Data Transfer Object cho thông tin phòng ban.
 * Chứa dữ liệu phòng ban được trả về từ service.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
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