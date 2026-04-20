package com.luvina.la.service;

import com.luvina.la.dto.CertificationDTO;
import java.util.List;

/**
 * Service xử lý nghiệp vụ chứng chỉ.
 */
public interface CertificationService {

    /**
     * Lấy danh sách tất cả chứng chỉ trong hệ thống.
     *
     * @return danh sách CertificationDTO
     */
    List<CertificationDTO> getCertifications();
}
