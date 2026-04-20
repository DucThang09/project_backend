package com.luvina.la.service;
/**
 * Giao diện dịch vụ xử lý nghiệp vụ liên quan đến phòng ban.
 * Cung cấp các phương thức để truy vấn danh sách phòng ban.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
import com.luvina.la.dto.DepartmentDTO;
import java.util.List;

/**
 * Service xử lý nghiệp vụ phòng ban.
 * Định nghĩa phương thức để lấy danh sách tất cả phòng ban.
 */
public interface DepartmentService {

    /**
     * Lấy danh sách tất cả phòng ban trong hệ thống.
     *
     * @return Danh sách DepartmentDTO chứa thông tin các phòng ban
     */
    List<DepartmentDTO> getDepartments();
}
