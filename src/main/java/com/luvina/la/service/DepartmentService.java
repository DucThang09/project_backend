/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * DepartmentService.java, 10/05/2026 tdthang
 */
package com.luvina.la.service;

import com.luvina.la.dto.DepartmentDTO;
import java.util.List;

/**
 * Service xử lý nghiệp vụ phòng ban.
 * @author tdthang
 */
public interface DepartmentService {

    /**
     * Lấy danh sách tất cả phòng ban trong hệ thống.
     *
     * @return danh sách DepartmentDTO chứa thông tin các phòng ban
     */
    List<DepartmentDTO> getDepartments();
}
