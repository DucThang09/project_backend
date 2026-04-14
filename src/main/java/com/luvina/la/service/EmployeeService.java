package com.luvina.la.service;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * sample.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.EmployeeDTO;
import java.util.List;

/** Service xử lý nghiệp vụ nhân viên. */
public interface EmployeeService {

    /** Đếm tổng số nhân viên. */
    Long getTotalEmployees(Long departmentId, String employeeName);

    /** Lấy danh sách nhân viên. */
    List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit
    );
}
