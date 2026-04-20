package com.luvina.la.repository;

import com.luvina.la.entity.Certification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository để truy cập dữ liệu chứng chỉ.
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
