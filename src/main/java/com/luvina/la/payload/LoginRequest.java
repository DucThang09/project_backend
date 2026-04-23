package com.luvina.la.payload;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import lombok.Data;

/**
 * Payload nhận thông tin đăng nhập từ client.
 */
@Data
public class LoginRequest {

    /** Tên đăng nhập của người dùng. */
    private String username;

    /** Mật khẩu của người dùng. */
    private String password;
}
