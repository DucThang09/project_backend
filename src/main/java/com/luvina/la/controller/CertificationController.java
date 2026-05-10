/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * CertificationController.java, 10/05/2026 tdthang
 */
package com.luvina.la.controller;

import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.payload.response.CertificationResponse;
import com.luvina.la.service.CertificationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý API chứng chỉ.
 * Cung cấp danh sách chứng chỉ để màn hình thêm/sửa nhân viên sử dụng.
 * @author tdthang
 */
@RestController
@RequestMapping("/certification")
public class CertificationController {

    private final CertificationService certificationService;
    /**
     * Constructor để inject service chứng chỉ.
     *
     * @param certificationService service xử lý nghiệp vụ chứng chỉ
     */
    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    /**
     * Lấy danh sách chứng chỉ đang có trong hệ thống.
     *
     * @return response chứa danh sách chứng chỉ hoặc lỗi hệ thống
     */
    @GetMapping
    public ResponseEntity<CertificationResponse> getCertifications() {
        List<CertificationDTO> certifications = certificationService.getCertifications();
        return ResponseEntity.ok(CertificationResponse.success(certifications));
    }
}
