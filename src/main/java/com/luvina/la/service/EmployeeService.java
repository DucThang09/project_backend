/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeService.java, 10/05/2026 tdthang
 */
package com.luvina.la.service;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.payload.request.EmployeeValidationRequest;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ nhân viên.
 *
 * @author tdthang
 */
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
     * @param departmentId         ID phòng ban cần lọc
     * @param employeeName         tên nhân viên cần tìm
     * @param ordEmployeeName      thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate           thứ tự sắp xếp theo ngày hết hạn chứng chỉ
     * @param offset               vị trí bắt đầu lấy dữ liệu
     * @param limit                số bản ghi tối đa cần lấy
     * @return danh sách nhân viên phù hợp
     */
    List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit);

    /**
     * Lấy thông tin chi tiết của một nhân viên theo ID.
     *
     * @param employeeId ID nhân viên cần lấy chi tiết
     * @return thông tin chi tiết nếu tồn tại
     */
    Optional<EmployeeDetailDTO> getEmployeeDetail(Long employeeId);

    /**
     * Thêm mới nhân viên và thông tin chứng chỉ nếu có.
     *
     * @param employeeValidationRequest dữ liệu nhân viên đã được validate
     *
     * @return giá trị trả về sau khi xử lý
     */
    Long addEmployee(EmployeeValidationRequest employeeValidationRequest);

    /**
     * Cập nhật nhân viên theo ID và ghi đè lại thông tin chứng chỉ hiện tại.
     *
     * @param employeeId                ID nhân viên cần cập nhật
     * @param employeeValidationRequest dữ liệu nhân viên đã được validate
     */
    void updateEmployee(Long employeeId, EmployeeValidationRequest employeeValidationRequest);

    /**
     * Xóa nhân viên theo ID.
     *
     * @param employeeId ID nhân viên cần xóa
     *
     * @return giá trị trả về sau khi xử lý
     */
    boolean deleteEmployee(Long employeeId);
}
