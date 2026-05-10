/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeCertificationRepository.java, 10/05/2026 tdthang
 */
package com.luvina.la.repository;

import com.luvina.la.entity.EmployeeCertification;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository truy cập dữ liệu employee certification.
 * Dùng để lưu hoặc xóa thông tin chứng chỉ gắn với nhân viên.
 * @author tdthang
 */
@Repository
public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {

    /**
     * Xóa toàn bộ chứng chỉ đang gắn với một nhân viên theo employee ID.
     *
     * @param employeeId ID nhân viên cần xóa chứng chỉ
     */
    @Transactional
    void deleteByEmployeeEmployeeId(Long employeeId);
}
