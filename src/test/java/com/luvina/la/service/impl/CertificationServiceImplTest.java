package com.luvina.la.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.repository.CertificationRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CertificationServiceImplTest {

    @Mock
    private CertificationRepository certificationRepository;

    private CertificationServiceImpl certificationService;

    @BeforeEach
    void setUp() {
        certificationService = new CertificationServiceImpl(certificationRepository);
    }

    @Test
    void shouldMapCertificationEntitiesToDtos() {
        Certification certification = new Certification();
        certification.setCertificationId(1L);
        certification.setCertificationName("N1");

        when(certificationRepository.findAllByOrderByCertificationIdAsc())
                .thenReturn(List.of(certification));

        List<CertificationDTO> result = certificationService.getCertifications();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCertificationId());
        assertEquals("N1", result.get(0).getCertificationName());
    }
}
