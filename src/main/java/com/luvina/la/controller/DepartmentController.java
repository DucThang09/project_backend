/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * DepartmentController.java, 10/05/2026 tdthang
 */
package com.luvina.la.controller;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.payload.response.DepartmentResponse;
import com.luvina.la.service.DepartmentService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý API phòng ban.
 * Cung cấp danh sách phòng ban cho màn hình tìm kiếm và màn hình thêm/sửa nhân viên.
 * @author tdthang
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService departmentService;
    /**
     * Constructor để inject service phòng ban.
     *
     * @param departmentService service xử lý nghiệp vụ phòng ban
     */
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * Lấy danh sách phòng ban đang có trong hệ thống.
     *
     * @return response chứa danh sách phòng ban hoặc lỗi hệ thống
     */
    @GetMapping
    public ResponseEntity<DepartmentResponse> getDepartments() {
        List<DepartmentDTO> departments = departmentService.getDepartments();
        return ResponseEntity.ok(DepartmentResponse.success(departments));
    }
}
