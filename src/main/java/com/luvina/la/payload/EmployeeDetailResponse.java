package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDetailDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Payload tra ve cho API chi tiet nhan vien.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDetailResponse {

    private Integer code;
    private EmployeeDetailDTO employee;
    private Message message;

    /**
     * Inner class chua ma message va tham so de format.
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
     * Tao response thanh cong cho API chi tiet nhan vien.
     *
     * @param employee thong tin chi tiet nhan vien
     * @return response voi ma 200
     */
    public static EmployeeDetailResponse success(EmployeeDetailDTO employee) {
        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setCode(HttpStatus.OK.value());
        response.setEmployee(employee);
        return response;
    }

    /**
     * Tao response loi.
     *
     * @param errorCode ma loi
     * @param params tham so dung de format message
     * @return response voi ma 500 va message loi
     */
    public static EmployeeDetailResponse error(String errorCode, List<String> params) {
        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
