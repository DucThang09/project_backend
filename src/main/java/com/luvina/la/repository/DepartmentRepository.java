package com.luvina.la.repository;

import com.luvina.la.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository thao tác dữ liệu phòng ban.
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