package com.luvina.la.service;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.EmployeeValidationRequest;
import java.util.List;

public interface EmployeeService {
    /**
     * Đếm tổng số nhân viên theo điều kiện tìm kiếm hiện tại.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm
     * @return tổng số bản ghi phù hợp
     */
    Long getTotalEmployees(Long departmentId, String employeeName);

    /**
     * Lấy danh sách nhân viên theo điều kiện tìm kiếm, sắp xếp và phân trang.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm
     * @param ordEmployeeName thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate thứ tự sắp xếp theo ngày hết hạn chứng chỉ
     * @param offset vị trí bắt đầu lấy dữ liệu
     * @param limit số bản ghi tối đa cần lấy
     * @return danh sách nhân viên phù hợp
     */
    List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit
    );

    /**
     * Thêm mới nhân viên và thông tin chứng chỉ nếu có.
     *
     * @param request dữ liệu nhân viên đã được validate
     */
    void addEmployee(EmployeeValidationRequest request);

    /**
     * Cập nhật nhân viên theo ID và ghi đè lại thông tin chứng chỉ hiện tại.
     *
     * @param employeeId ID nhân viên cần cập nhật
     * @param request dữ liệu nhân viên đã được validate
     */
    void updateEmployee(Long employeeId, EmployeeValidationRequest request);
}
