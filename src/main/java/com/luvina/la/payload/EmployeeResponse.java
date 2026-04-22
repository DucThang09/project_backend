package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload cho phản hồi danh sách nhân viên.
 * Chứa dữ liệu nhân viên hoặc thông báo lỗi.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {

    /** Mã trạng thái HTTP. */
    private Integer code;

    /** Tổng số bản ghi phù hợp với điều kiện lọc. */
    private Long totalRecords;

    /** Danh sách nhân viên. */
    private List<EmployeeDTO> employees;

    /** Thông báo lỗi hoặc mã message từ file properties. */
    private Message message;

    /**
     * Lớp inner chứa thông tin message.
     * Sử dụng để trả về mã message và tham số từ file properties.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        /** Mã message trong file properties. */
        private String code;

        /** Danh sách tham số để thay thế vào message. */
        private List<String> params;
    }

    /**
     * Tạo response thành công với danh sách nhân viên.
     *
     * @param totalRecords Tổng số bản ghi
     * @param employees Danh sách nhân viên
     * @return EmployeeResponse với mã 200
     */
    public static EmployeeResponse success(Long totalRecords, List<EmployeeDTO> employees) {
        EmployeeResponse response = new EmployeeResponse();
        response.setCode(HttpStatus.OK.value());
        response.setTotalRecords(totalRecords);
        response.setEmployees(employees);
        return response;
    }

    /**
     * Tạo response thành công không kèm dữ liệu danh sách.
     *
     * @return EmployeeResponse với mã 200
     */
    public static EmployeeResponse success() {
        EmployeeResponse response = new EmployeeResponse();
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    /**
     * Tạo response thành công với danh sách nhân viên và mã message.
     *
     * @param totalRecords Tổng số bản ghi
     * @param employees Danh sách nhân viên
     * @param messageCode Mã message từ properties
     * @return EmployeeResponse với mã 200 và message
     */
    public static EmployeeResponse success(Long totalRecords, List<EmployeeDTO> employees, String messageCode) {
        EmployeeResponse response = success(totalRecords, employees);
        response.setMessage(new Message(messageCode, List.of()));
        return response;
    }

    /**
     * Tạo response lỗi.
     *
     * @param errorCode Mã lỗi từ properties
     * @param params Tham số cho message lỗi
     * @return EmployeeResponse với mã 500 và message lỗi
     */
    public static EmployeeResponse error(String errorCode, List<String> params) {
        EmployeeResponse response = new EmployeeResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
