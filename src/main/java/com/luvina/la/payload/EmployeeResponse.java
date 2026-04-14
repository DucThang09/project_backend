package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {

    // code, totalRecords, employees dùng cho response thành công.
    private Integer code;
    private Long totalRecords;
    private List<EmployeeDTO> employees;

    // message dùng cho response lỗi hoặc các trường hợp cần trả mã message.
    private Message message;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        // code là mã message trong file properties, params là tham số đi kèm.
        private String code;
        private List<String> params;
    }

    // Response thành công
    public static EmployeeResponse success(Long totalRecords, List<EmployeeDTO> employees) {
        EmployeeResponse response = new EmployeeResponse();
        response.setCode(HttpStatus.OK.value());
        response.setTotalRecords(totalRecords);
        response.setEmployees(employees);
        return response;
    }

    // Response thành công nhưng cần trả thêm mã message
    public static EmployeeResponse success(Long totalRecords, List<EmployeeDTO> employees, String messageCode) {
        EmployeeResponse response = success(totalRecords, employees);
        response.setMessage(new Message(messageCode, List.of()));
        return response;
    }

    // Response lỗi
    public static EmployeeResponse error(String errorCode, List<String> params) {
        EmployeeResponse response = new EmployeeResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
