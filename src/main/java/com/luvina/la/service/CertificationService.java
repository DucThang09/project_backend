/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * CertificationService.java, 10/05/2026 tdthang
 */
package com.luvina.la.service;

import com.luvina.la.dto.CertificationDTO;
import java.util.List;

/**
 * Service xử lý nghiệp vụ chứng chỉ.
 * @author tdthang
 */
public interface CertificationService {

    /**
     * Lấy danh sách tất cả chứng chỉ trong hệ thống.
     *
     * @return danh sách CertificationDTO
     */
    List<CertificationDTO> getCertifications();
}
