package com.luvina.la.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.payload.CertificationResponse;
import com.luvina.la.service.CertificationService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CertificationControllerTest {

    @Mock
    private CertificationService certificationService;

    private CertificationController certificationController;

    @BeforeEach
    void setUp() {
        certificationController = new CertificationController(certificationService);
    }

    @Test
    void shouldReturnCertificationListWhenServiceSucceeds() {
        CertificationDTO dto = new CertificationDTO();
        dto.setCertificationId(1L);
        dto.setCertificationName("N1");

        when(certificationService.getCertifications()).thenReturn(List.of(dto));

        ResponseEntity<CertificationResponse> response = certificationController.getCertifications();

        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getCertifications());
        assertEquals(1, response.getBody().getCertifications().size());
        assertEquals("N1", response.getBody().getCertifications().get(0).getCertificationName());
    }

    @Test
    void shouldReturnSystemErrorWhenServiceThrowsException() {
        when(certificationService.getCertifications()).thenThrow(new RuntimeException("boom"));

        ResponseEntity<CertificationResponse> response = certificationController.getCertifications();

        assertEquals(500, response.getBody().getCode());
        assertNotNull(response.getBody().getMessage());
        assertEquals("ER023", response.getBody().getMessage().getCode());
        assertTrue(response.getBody().getMessage().getParams().isEmpty());
        assertEquals(Collections.emptyList(), response.getBody().getMessage().getParams());
    }
}
