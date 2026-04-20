package com.luvina.la.service.impl;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implementation c?a EmployeeService.
 * X? lý logic nghi?p v? liên quan d?n nhân viên v?i s?p x?p và phân trang.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    /** Repository d? truy c?p d? li?u nhân viên. */
    private final EmployeeRepository employeeRepository;

    /**
     * Constructor d? inject dependencies.
     *
     * @param employeeRepository Repository cho nhân viên
     */
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getTotalEmployees(Long departmentId, String employeeName) {
        return employeeRepository.countTotalEmployees(departmentId, employeeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit) {

        int finalLimit = (limit == null || limit <= 0) ? Constants.DEFAULT_LIMIT : limit;
        int finalOffset = (offset == null || offset < 0) ? Constants.DEFAULT_OFFSET : offset;

        List<Object[]> results = employeeRepository.findEmployees(
                departmentId,
                employeeName,
                ordEmployeeName,
                ordCertificationName,
                ordEndDate,
                finalLimit,
                finalOffset
        );

        List<EmployeeDTO> dtos = new ArrayList<>(results.size());
        for (Object[] row : results) {
            dtos.add(EmployeeDTO.fromRow(row));
        }
        return dtos;
    }
}
