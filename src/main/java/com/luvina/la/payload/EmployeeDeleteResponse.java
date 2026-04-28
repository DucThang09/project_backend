package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class EmployeeDeleteResponse {

    private Integer code;
    private String employeeId;
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

    public static EmployeeDeleteResponse success(String employeeId) {
        EmployeeDeleteResponse response = new EmployeeDeleteResponse();
        response.setCode(HttpStatus.OK.value());
        response.setEmployeeId(employeeId);
        response.setMessage(new Message("MSG003", List.of()));
        return response;
    }

    public static EmployeeDeleteResponse error(String employeeId, String errorCode, List<String> params) {
        EmployeeDeleteResponse response = new EmployeeDeleteResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setEmployeeId(employeeId);
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}
