/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * LoginRequest.java, 10/05/2026 tdthang
 */
package com.luvina.la.payload.request;

import lombok.Data;

/**
 * Payload nhận thông tin đăng nhập từ client.
 * @author tdthang
 */
@Data
public class LoginRequest {

    private String username;
    private String password;
}
