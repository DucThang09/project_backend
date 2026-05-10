/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeDeleteResponse.java, 10/05/2026 tdthang
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
 * dữ liệu response cho EmployeeDeleteResponse.
 *
 * @author tdthang
 */

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDeleteResponse {

    private Integer code;
    private String employeeId;
    private Message message;
    /**
     * Mô tả chức năng của Message.
     *
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
     * Tạo response thành công.
     *
     * @param employeeId tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */

    public static EmployeeDeleteResponse success(String employeeId) {
        EmployeeDeleteResponse response = new EmployeeDeleteResponse();
        response.setCode(HttpStatus.OK.value());
        response.setEmployeeId(employeeId);
        response.setMessage(new Message("MSG003", List.of()));
        return response;
    }
    /**
     * Tạo response lỗi.
     *
     * @param employeeId tham số đầu vào của method
     * @param errorCode tham số đầu vào của method
     * @param params tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */

    public static EmployeeDeleteResponse error(String employeeId, String errorCode, List<String> params) {
        EmployeeDeleteResponse response = new EmployeeDeleteResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setEmployeeId(employeeId);
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
