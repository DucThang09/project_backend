package com.luvina.la.repository;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.entity.EmployeeCertification;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository truy cập dữ liệu employee certification.
 * Dùng để lưu hoặc xóa thông tin chứng chỉ gắn với nhân viên.
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
