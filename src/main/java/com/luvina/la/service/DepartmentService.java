package com.luvina.la.service;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.DepartmentDTO;
import java.util.List;

/**
 * Service xử lý nghiệp vụ phòng ban.
 */
public interface DepartmentService {

    /**
     * Lấy danh sách tất cả phòng ban trong hệ thống.
     *
     * @return danh sách DepartmentDTO chứa thông tin các phòng ban
     */
    List<DepartmentDTO> getDepartments();
}
