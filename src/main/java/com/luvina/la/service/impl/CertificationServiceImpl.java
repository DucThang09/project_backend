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
 */
@Service
public class CertificationServiceImpl implements CertificationService {

    /** Repository để truy cập dữ liệu chứng chỉ. */
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
     * {@inheritDoc}
     */
    @Override
    public List<CertificationDTO> getCertifications() {
        List<Certification> certifications = certificationRepository.findAllByOrderByCertificationIdAsc();
        List<CertificationDTO> dtos = new ArrayList<>(certifications.size());

        for (Certification certification : certifications) {
            CertificationDTO dto = new CertificationDTO();
            dto.setCertificationId(certification.getCertificationId());
            dto.setCertificationName(certification.getCertificationName());
            dtos.add(dto);
        }

        return dtos;
    }
}
