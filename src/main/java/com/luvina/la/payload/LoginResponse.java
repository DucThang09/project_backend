package com.luvina.la.payload;
/**
 * Payload cho phản hồi đăng nhập.
 * Chứa token JWT hoặc thông báo lỗi.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Lớp chứa phản hồi từ server sau khi xử lý đăng nhập.
 * Có thể chứa token truy cập hoặc danh sách lỗi.
 */
@Data
public class LoginResponse {

    /** Token truy cập JWT. */
    private String accessToken;

    /** Loại token (luôn là "Bearer"). */
    private String tokenType;

    /** Map chứa các thông báo lỗi nếu đăng nhập thất bại. */
    private Map<String, String> errors = new HashMap<>();

    /**
     * Constructor cho trường hợp đăng nhập thành công.
     *
     * @param accessToken Token JWT được tạo
     */
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    /**
     * Constructor cho trường hợp đăng nhập thất bại.
     *
     * @param errors Map chứa thông báo lỗi
     */
    public LoginResponse(Map<String, String> errors) {
        this.errors = errors;
    }

}
