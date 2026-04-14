package com.luvina.la.payload;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * sample.java, April 13, 2026 tdthang
 */
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private Map<String, String> errors = new HashMap<>();

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    public LoginResponse(Map<String, String> errors) {
        this.errors = errors;
    }

}
