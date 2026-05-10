/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * DepartmentServiceImpl.java, 10/05/2026 tdthang
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Department;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Cài đặt nghiệp vụ cho DepartmentServiceImpl.
 *
 * @author tdthang
 */


@Service
public class DepartmentServiceImpl implements DepartmentService {

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
     *
     * @return giá trị trả về sau khi xử lý
     */
    @Override
    public List<DepartmentDTO> getDepartments() {
        List<Department> departments = departmentRepository.findAllByOrderByDepartmentIdAsc();

        // Chuyển entity sang DTO để trả dữ liệu gọn cho controller.
        List<DepartmentDTO> departmentDtos = new ArrayList<>(departments.size());

        for (Department department : departments) {
            DepartmentDTO departmentDto = new DepartmentDTO();
            departmentDto.setDepartmentId(department.getDepartmentId());
            departmentDto.setDepartmentName(department.getDepartmentName());
            departmentDtos.add(departmentDto);
        }

        return departmentDtos;
    }
}
