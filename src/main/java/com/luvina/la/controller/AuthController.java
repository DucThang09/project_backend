/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * AuthController.java, 10/05/2026 tdthang
 */
package com.luvina.la.controller;

import com.luvina.la.config.jwt.AuthUserDetails;
import com.luvina.la.config.jwt.JwtTokenProvider;
import com.luvina.la.config.jwt.UserDetailsServiceImpl;
import com.luvina.la.payload.request.LoginRequest;
import com.luvina.la.payload.response.LoginResponse;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các API xác thực người dùng.
 * Bao gồm đăng nhập và kiểm tra token JWT.
 * @author tdthang
 */
@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
            if (!isAdmin(authentication)) {
                SecurityContextHolder.clearContext();
                errors.put("code", "100");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(errors));
            }
            String accessToken = tokenProvider.generateToken((AuthUserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(new LoginResponse(accessToken));
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            log.warn(ex.getMessage());
            errors.put("code", "100");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(errors));
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

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
