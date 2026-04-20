package com.luvina.la.service;
/**
 * Giao diện dịch vụ xử lý nghiệp vụ liên quan đến nhân viên.
 * Cung cấp các phương thức để truy vấn và thao tác dữ liệu nhân viên.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
import com.luvina.la.dto.EmployeeDTO;
import java.util.List;

/**
 * Service xử lý nghiệp vụ nhân viên.
 * Định nghĩa các phương thức để lấy danh sách nhân viên và đếm số lượng.
 */
public interface EmployeeService {

    /**
     * Đếm tổng số nhân viên theo điều kiện lọc.
     *
     * @param departmentId ID phòng ban (có thể null để lấy tất cả)
     * @param employeeName Tên nhân viên để tìm kiếm (có thể null)
     * @return Tổng số nhân viên phù hợp với điều kiện
     */
    Long getTotalEmployees(Long departmentId, String employeeName);

    /**
     * Lấy danh sách nhân viên theo điều kiện lọc và sắp xếp.
     *
     * @param departmentId ID phòng ban (có thể null để lấy tất cả)
     * @param employeeName Tên nhân viên để tìm kiếm (có thể null)
     * @param ordEmployeeName Thứ tự sắp xếp theo tên nhân viên (ASC/DESC)
     * @param ordCertificationName Thứ tự sắp xếp theo tên chứng chỉ (ASC/DESC)
     * @param ordEndDate Thứ tự sắp xếp theo ngày kết thúc chứng chỉ (ASC/DESC)
     * @param offset Vị trí bắt đầu lấy dữ liệu (phân trang)
     * @param limit Số lượng bản ghi tối đa trả về
     * @return Danh sách EmployeeDTO phù hợp với điều kiện
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
}
