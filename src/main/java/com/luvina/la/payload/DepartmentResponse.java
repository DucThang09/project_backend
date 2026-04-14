package com.luvina.la.payload;

/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * DepartmentResponse.java, April 13, 2026 tdthang
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.DepartmentDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/** Payload trả về cho API danh sách phòng ban. */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponse {

    private Integer code;
    private List<DepartmentDTO> departments;
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

    /** Tạo response thành công. */
    public static DepartmentResponse success(List<DepartmentDTO> departments) {
        DepartmentResponse response = new DepartmentResponse();
        response.setCode(HttpStatus.OK.value());
        response.setDepartments(departments);
        return response;
    }

    /** Tạo response lỗi. */
    public static DepartmentResponse error(String errorCode, List<String> params) {
        DepartmentResponse response = new DepartmentResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}