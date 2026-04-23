package com.luvina.la.controller;

/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * DepartmentController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.payload.DepartmentResponse;
import com.luvina.la.service.DepartmentService;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý API phòng ban.
 */
@RestController
@RequestMapping("/user")
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
     * Lấy danh sách phòng ban.
     *
     * @return response chứa danh sách phòng ban hoặc lỗi hệ thống
     */
    @GetMapping("/departments")
    public ResponseEntity<DepartmentResponse> getDepartments() {
        try {
            List<DepartmentDTO> departments = departmentService.getDepartments();
            return ResponseEntity.ok(DepartmentResponse.success(departments));
        } catch (Exception exception) {
            return ResponseEntity.ok(
                    DepartmentResponse.error("ER023", Collections.emptyList())
            );
        }
    }
}
