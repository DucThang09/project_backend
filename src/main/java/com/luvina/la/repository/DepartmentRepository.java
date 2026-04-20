package com.luvina.la.repository;

import com.luvina.la.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository để truy cập dữ liệu phòng ban.
 * Cung cấp các phương thức truy vấn cơ bản và tùy chỉnh cho bảng departments.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Lấy danh sách tất cả phòng ban, sắp xếp tăng dần theo mã phòng ban.
     *
     * @return Danh sách phòng ban đã sắp xếp.
     */
    List<Department> findAllByOrderByDepartmentIdAsc();
}