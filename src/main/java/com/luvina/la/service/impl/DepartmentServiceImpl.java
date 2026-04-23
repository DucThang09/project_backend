package com.luvina.la.service.impl;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Department;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DepartmentServiceImpl implements DepartmentService {

    /** Repository để truy cập dữ liệu phòng ban. */
    private final DepartmentRepository departmentRepository;

    /**
     *
     * @param departmentRepository Repository cho phòng ban
     */
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * Lấy tất cả phòng ban và sắp xếp theo ID tăng dần.
     */
    @Override
    public List<DepartmentDTO> getDepartments() {
        List<Department> departments = departmentRepository.findAllByOrderByDepartmentIdAsc();

        // Chuyển entity sang DTO để trả dữ liệu gọn cho controller.
        List<DepartmentDTO> dtos = new ArrayList<>(departments.size());

        for (Department dept : departments) {
            DepartmentDTO dto = new DepartmentDTO();
            dto.setDepartmentId(dept.getDepartmentId());
            dto.setDepartmentName(dept.getDepartmentName());
            dtos.add(dto);
        }

        return dtos;
    }
}
