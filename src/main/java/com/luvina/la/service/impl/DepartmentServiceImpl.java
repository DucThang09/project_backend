package com.luvina.la.service.impl;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Department;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service xử lý logic liên quan đến phòng ban.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * Lấy danh sách tất cả phòng ban
     */
    @Override
    public List<DepartmentDTO> getDepartments() {
        List<Department> departments = departmentRepository.findAllByOrderByDepartmentIdAsc();

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