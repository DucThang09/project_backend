package com.luvina.la.payload;

/**
 * Payload cho phản hồi danh sách phòng ban.
 * Chứa dữ liệu phòng ban hoặc thông báo lỗi.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
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
 * Chứa danh sách phòng ban hoặc thông báo lỗi.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponse {

    /** Mã trạng thái HTTP. */
    private Integer code;

    /** Danh sách phòng ban. */
    private List<DepartmentDTO> departments;

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
     * Tạo response thành công với danh sách phòng ban.
     *
     * @param departments Danh sách phòng ban
     * @return DepartmentResponse với mã 200
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
     * @param errorCode Mã lỗi từ properties
     * @param params Tham số cho message lỗi
     * @return DepartmentResponse với mã 500 và message lỗi
     */
    public static DepartmentResponse error(String errorCode, List<String> params) {
        DepartmentResponse response = new DepartmentResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new Message(errorCode, params));
        return response;
    }
}