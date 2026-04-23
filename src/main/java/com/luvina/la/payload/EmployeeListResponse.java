package com.luvina.la.payload;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload trả về cho API danh sách nhân viên.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeListResponse {

    /** Mã trạng thái HTTP. */
    private Integer code;

    /** Tổng số bản ghi tìm được. */
    private Long totalRecords;

    /** Danh sách nhân viên trả về cho client. */
    private List<EmployeeDTO> employees;

    /** Message trả về trong các trường hợp đặc biệt hoặc lỗi. */
    private Message message;

    /**
     * Inner class chứa mã message và tham số để format.
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
     * Tạo response thành công với danh sách nhân viên.
     *
     * @param totalRecords tổng số bản ghi
     * @param employees danh sách nhân viên
     * @return response với mã 200
     */
    public static EmployeeListResponse success(Long totalRecords, List<EmployeeDTO> employees) {
        EmployeeListResponse response = new EmployeeListResponse();
        response.setCode(HttpStatus.OK.value());
        response.setTotalRecords(totalRecords);
        response.setEmployees(employees);
        return response;
    }

    /**
     * Tạo response thành công kèm mã message.
     *
     * @param totalRecords tổng số bản ghi
     * @param employees danh sách nhân viên
     * @param messageCode mã message cần trả thêm
     * @return response với mã 200 và message
     */
    public static EmployeeListResponse success(Long totalRecords, List<EmployeeDTO> employees, String messageCode) {
        EmployeeListResponse response = success(totalRecords, employees);
        response.setMessage(new Message(messageCode, List.of()));
        return response;
    }

    /**
     * Tạo response lỗi.
     *
     * @param errorCode mã lỗi
     * @param params tham số dùng để format message
     * @return response với mã 500 và message lỗi
     */
    public static EmployeeListResponse error(String errorCode, List<String> params) {
        EmployeeListResponse response = new EmployeeListResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
