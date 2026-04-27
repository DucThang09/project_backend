package com.luvina.la.controller;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * AuthController.java, April 13, 2026 tdthang
 */

import com.luvina.la.config.jwt.AuthUserDetails;
import com.luvina.la.config.jwt.JwtTokenProvider;
import com.luvina.la.config.jwt.UserDetailsServiceImpl;
import com.luvina.la.payload.LoginRequest;
import com.luvina.la.payload.LoginResponse;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các API xác thực người dùng.
 * Bao gồm đăng nhập và kiểm tra token JWT.
 */
@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /** Component tạo và kiểm tra JWT. */
    final JwtTokenProvider tokenProvider;

    /** Component xác thực thông tin đăng nhập của người dùng. */
    final AuthenticationManager authenticationManager;

    /** Service load thông tin người dùng phục vụ xác thực. */
    final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor để inject các dependency phục vụ xác thực và tạo JWT.
     *
     * @param authenticationManager component xác thực username/password
     * @param jwtTokenProvider component tạo JWT
     * @param userDetailsService service load thông tin người dùng
     */
    AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Đăng nhập người dùng và trả về JWT nếu thông tin đăng nhập hợp lệ.
     *
     * @param loginRequest thông tin đăng nhập từ client
     * @param request request hiện tại
     * @return response chứa JWT nếu thành công hoặc mã lỗi nếu thất bại
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.generateToken((AuthUserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(new LoginResponse(accessToken));
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            log.warn(ex.getMessage());
            errors.put("code", "100");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(errors));
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            errors.put("code", "000");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(errors));
        }
    }

    /**
     * Kiểm tra token JWT hiện tại còn hợp lệ.
     *
     * @return map chứa thông báo xác nhận token hợp lệ
     */
    @RequestMapping("/test-auth")
    public Map<String, String> testAuth() {
        Map<String, String> testData = new HashMap<>();
        testData.put("msg", "Token is valid");
        return testData;
    }
}
