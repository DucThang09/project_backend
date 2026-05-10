/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeDetailResponse.java, 10/05/2026 tdthang
 */
package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDetailDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload trả về cho API lấy chi tiết nhân viên.
 * Dùng cho màn ADM003 và màn ADM004 khi mở ở mode chỉnh sửa.
 * @author tdthang
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDetailResponse {

    private Integer code;
    /** Thông tin chi tiết nhân viên trả về khi xử lý thành công. */
    private EmployeeDetailDTO employee;
    /** Thông tin message lỗi trả về khi xử lý thất bại. */
    private Message message;
    /**
     * Thông tin message lỗi trả về cho frontend.
     * Frontend dùng code và params để format nội dung lỗi hiển thị.
     * @author tdthang
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        private String code;
        /** Danh sách tham số dùng để format message lỗi. */
        private List<String> params;
    }
    /**
         * Tạo response thành công cho API chi tiết nhân viên.
         *
         * @param employee thông tin chi tiết nhân viên
         * @return response với code 200 và dữ liệu nhân viên
         */
    public static EmployeeDetailResponse success(EmployeeDetailDTO employee) {
        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setCode(HttpStatus.OK.value());
        response.setEmployee(employee);
        return response;
    }

    /**
     * Tạo response lỗi cho API chi tiết nhân viên.
     *
     * @param errorCode mã lỗi trả về cho frontend
     * @param params tham số dùng để format message lỗi
     * @return response với code 500 và message lỗi
     */
    public static EmployeeDetailResponse error(String errorCode, List<String> params) {
        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
