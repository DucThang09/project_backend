/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * CertificationServiceImpl.java, 10/05/2026 tdthang
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.service.CertificationService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implementation của CertificationService.
 * @author tdthang
 */
@Service
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    /**
     * Constructor để inject dependencies.
     *
     * @param certificationRepository repository cho chứng chỉ
     */
    public CertificationServiceImpl(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    /**
     *
     * @return giá trị trả về sau khi xử lý
     */
    @Override
    public List<CertificationDTO> getCertifications() {
        List<Certification> certifications = certificationRepository.findAllByOrderByCertificationIdAsc();

        // Chuyển entity sang DTO để controller trả về đúng dữ liệu cần hiển thị.
        List<CertificationDTO> certificationDtos = new ArrayList<>(certifications.size());

        for (Certification certification : certifications) {
            CertificationDTO certificationDto = new CertificationDTO();
            certificationDto.setCertificationId(certification.getCertificationId());
            certificationDto.setCertificationName(certification.getCertificationName());
            certificationDtos.add(certificationDto);
        }

        return certificationDtos;
    }
}
