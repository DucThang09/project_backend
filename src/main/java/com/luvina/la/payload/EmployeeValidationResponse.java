package com.luvina.la.payload;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload trả về cho API validate/add/update employee.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeValidationResponse {

    /** Mã trạng thái HTTP. */
    private Integer code;

    /** Message lỗi trả về khi request không hợp lệ. */
    private Message message;

    /**
     * Inner class chứa mã lỗi và tham số để format message.
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
     * Kết quả validate giữa validator và controller.
     */
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {
        private final String code;
        private final List<String> params;

        private ErrorResponse(String code, List<String> params) {
            this.code = code;
            this.params = params;
        }

        public static ErrorResponse valid() {
            return new ErrorResponse(String.valueOf(HttpStatus.OK.value()), List.of());
        }

        public static ErrorResponse invalid(String code, List<String> params) {
            return new ErrorResponse(code, params != null ? params : List.of());
        }

        public boolean isValid() {
            return String.valueOf(HttpStatus.OK.value()).equals(code);
        }
    }

    /**
     * Tạo response thành công.
     *
     * @return response với mã 200
     */
    public static EmployeeValidationResponse success() {
        EmployeeValidationResponse response = new EmployeeValidationResponse();
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    /**
     * Táº¡o response thÃ nh cÃ´ng kÃ¨m message.
     *
     * @param messageCode mÃ£ message tráº£ vá» cho frontend
     * @return response vá»›i mÃ£ 200 vÃ  message
     */
    /**
     * Tạo response lỗi.
     *
     * @param errorCode mã lỗi
     * @param params tham số dùng để format message
     * @return response với mã 500 và message lỗi
     */
    public static EmployeeValidationResponse error(String errorCode, List<String> params) {
        EmployeeValidationResponse response = new EmployeeValidationResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
