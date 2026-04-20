package com.luvina.la.service.impl;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Department;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của DepartmentService.
 * Xử lý logic nghiệp vụ liên quan đến phòng ban.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    /** Repository để truy cập dữ liệu phòng ban. */
    private final DepartmentRepository departmentRepository;

    /**
     * Constructor để inject dependencies.
     *
     * @param departmentRepository Repository cho phòng ban
     */
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * {@inheritDoc}
     * Lấy tất cả phòng ban và sắp xếp theo ID tăng dần.
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