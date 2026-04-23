package com.luvina.la.payload;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Payload trả về sau khi xử lý đăng nhập.
 * Có thể chứa access token hoặc danh sách lỗi.
 */
@Data
public class LoginResponse {

    /** Token truy cập JWT. */
    private String accessToken;

    /** Loại token, mặc định là Bearer. */
    private String tokenType;

    /** Danh sách lỗi khi đăng nhập thất bại. */
    private Map<String, String> errors = new HashMap<>();

    /**
     * Constructor dùng cho trường hợp đăng nhập thành công.
     *
     * @param accessToken token JWT được tạo
     */
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    /**
     * Constructor dùng cho trường hợp đăng nhập thất bại.
     *
     * @param errors danh sách lỗi trả về cho client
     */
    public LoginResponse(Map<String, String> errors) {
        this.errors = errors;
    }
}
