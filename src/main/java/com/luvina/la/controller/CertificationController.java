package com.luvina.la.controller;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.payload.CertificationResponse;
import com.luvina.la.service.CertificationService;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý API chứng chỉ.
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
     * Lấy danh sách chứng chỉ.
     *
     * @return response chứa danh sách chứng chỉ hoặc lỗi hệ thống
     */
    @GetMapping
    public ResponseEntity<CertificationResponse> getCertifications() {
        try {
            List<CertificationDTO> certifications = certificationService.getCertifications();
            return ResponseEntity.ok(CertificationResponse.success(certifications));
        } catch (Exception exception) {
            return ResponseEntity.ok(
                    CertificationResponse.error("ER023", Collections.emptyList())
            );
        }
    }
}
