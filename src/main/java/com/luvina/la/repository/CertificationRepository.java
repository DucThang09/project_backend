/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * CertificationRepository.java, 10/05/2026 tdthang
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Certification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository để truy cập dữ liệu chứng chỉ.
 * @author tdthang
 */
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    /**
     * Lấy danh sách tất cả chứng chỉ, sắp xếp tăng dần theo mã chứng chỉ.
     *
     * @return danh sách chứng chỉ đã sắp xếp
     */
    List<Certification> findAllByOrderByCertificationIdAsc();
}
