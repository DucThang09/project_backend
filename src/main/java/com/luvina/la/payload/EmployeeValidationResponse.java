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
