/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * DepartmentResponse.java, 10/05/2026 tdthang
 */
package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.DepartmentDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload trả về cho API danh sách phòng ban.
 * @author tdthang
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponse {

    private Integer code;
    /** Danh sách phòng ban. */
    private List<DepartmentDTO> departments;
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
     * Tạo response thành công với danh sách phòng ban.
     *
     * @param departments danh sách phòng ban
     * @return response với mã 200
     */
    public static DepartmentResponse success(List<DepartmentDTO> departments) {
        DepartmentResponse response = new DepartmentResponse();
        response.setCode(HttpStatus.OK.value());
        response.setDepartments(departments);
        return response;
    }

    /**
     * Tạo response lỗi.
     *
     * @param errorCode mã lỗi
     * @param params tham số dùng để format message
     * @return response với mã 500 và message lỗi
     */
    public static DepartmentResponse error(String errorCode, List<String> params) {
        DepartmentResponse response = new DepartmentResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
