package com.luvina.la.payload;

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
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificationResponse {

    /** Mã trạng thái HTTP. */
    private Integer code;

    /** Danh sách chứng chỉ. */
    private List<CertificationDTO> certifications;

    /** Thông báo lỗi hoặc mã message từ file properties. */
    private Message message;

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
     * @return CertificationResponse với mã 200
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
     * @param errorCode mã lỗi từ properties
     * @param params tham số cho message lỗi
     * @return CertificationResponse với mã 500 và message lỗi
     */
    public static CertificationResponse error(String errorCode, List<String> params) {
        CertificationResponse response = new CertificationResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
