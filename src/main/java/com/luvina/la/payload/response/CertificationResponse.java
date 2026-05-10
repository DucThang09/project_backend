/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * CertificationResponse.java, 10/05/2026 tdthang
 */
package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.CertificationDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload trả về cho API danh sách chứng chỉ.
 * @author tdthang
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificationResponse {

    private Integer code;
    /** Danh sách chứng chỉ. */
    private List<CertificationDTO> certifications;
    /** Message lỗi hoặc mã message trả về từ backend. */
    private Message message;
    /**
     * Inner class chứa mã message và danh sách tham số.
     * @author tdthang
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        private String code;
        private List<String> params;
    }

    /**
     * Tạo response thành công với danh sách chứng chỉ.
     *
     * @param certifications danh sách chứng chỉ
     * @return response với mã 200
     */
    public static CertificationResponse success(List<CertificationDTO> certifications) {
        CertificationResponse response = new CertificationResponse();
        response.setCode(HttpStatus.OK.value());
        response.setCertifications(certifications);
        return response;
    }

    /**
     * Tạo response lỗi.
     *
     * @param errorCode mã lỗi
     * @param params tham số dùng để format message
     * @return response với mã 500 và message lỗi
     */
    public static CertificationResponse error(String errorCode, List<String> params) {
        CertificationResponse response = new CertificationResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
