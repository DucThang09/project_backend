/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * LoginResponse.java, 10/05/2026 tdthang
 */
package com.luvina.la.payload.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Payload trả về sau khi xử lý đăng nhập.
 * Có thể chứa access token hoặc danh sách lỗi.
 * @author tdthang
 */
@Data
public class LoginResponse {

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
