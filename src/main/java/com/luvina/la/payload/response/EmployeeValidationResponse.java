/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeValidationResponse.java, 10/05/2026 tdthang
 */
package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload trả về cho API validate/add/update employee.
 * @author tdthang
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeValidationResponse {

    private Integer code;
    private Long employeeId;
    /** Message lỗi trả về khi request không hợp lệ. */
    private Message message;
    /**
     * Inner class chứa mã lỗi và tham số để format message.
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
     * Kết quả validate giữa validator và controller.
     * @author tdthang
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
        /**
         * Xử lý chức năng valid.
         *
         * @return giá trị trả về sau khi xử lý
         */

        public static ErrorResponse valid() {
            return new ErrorResponse(String.valueOf(HttpStatus.OK.value()), List.of());
        }
        /**
         * Xử lý chức năng invalid.
         *
         * @param code tham số đầu vào của method
         * @param params tham số đầu vào của method
         * @return giá trị trả về sau khi xử lý
         */

        public static ErrorResponse invalid(String code, List<String> params) {
            return new ErrorResponse(code, params != null ? params : List.of());
        }
        /**
         * Kiểm tra dữ liệu isValid.
         *
         * @return giá trị trả về sau khi xử lý
         */

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
     * Tạo response thành công.
     *
     * @param employeeId tham số đầu vào của method
     * @param messageCode tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */

    public static EmployeeValidationResponse success(Long employeeId, String messageCode) {
        EmployeeValidationResponse response = success();
        response.setEmployeeId(employeeId);
        response.setMessage(new Message(messageCode, List.of()));
        return response;
    }

    /**
     *
     * @param errorCode
     * @param params
     * @return
     */
    public static EmployeeValidationResponse error(String errorCode, List<String> params) {
        EmployeeValidationResponse response = new EmployeeValidationResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
