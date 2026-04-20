package com.luvina.la.payload;
/**
 * Payload cho yêu cầu đăng nhập.
 * Chứa thông tin xác thực từ client.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
import lombok.Data;

/**
 * Lớp chứa thông tin đăng nhập gửi từ client.
 * Bao gồm tên đăng nhập và mật khẩu.
 */
@Data
public class LoginRequest {

    /** Tên đăng nhập của người dùng. */
    private String username;

    /** Mật khẩu của người dùng. */
    private String password;
}
