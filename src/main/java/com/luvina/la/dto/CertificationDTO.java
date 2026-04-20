package com.luvina.la.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin chứng chỉ.
 */
@Getter
@Setter
public class CertificationDTO {

    /** ID duy nhất của chứng chỉ. */
    private Long certificationId;

    /** Tên chứng chỉ. */
    private String certificationName;
}
