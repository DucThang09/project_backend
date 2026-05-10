/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * GlobalExceptionHandler.java, 10/05/2026 tdthang
 */
package com.luvina.la.exception;

import com.luvina.la.payload.response.CertificationResponse;
import com.luvina.la.payload.response.DepartmentResponse;
import com.luvina.la.payload.response.EmployeeDeleteResponse;
import com.luvina.la.payload.response.EmployeeDetailResponse;
import com.luvina.la.payload.response.EmployeeListResponse;
import com.luvina.la.payload.response.EmployeeValidationResponse;
import com.luvina.la.payload.response.LoginResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Xử lý exception chung phát sinh từ controller và service.
 * @author tdthang
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý lỗi hệ thống chưa được bắt riêng ở controller.
     *
     * @param exception exception phát sinh trong quá trình xử lý request
     * @param request request hiện tại dùng để xác định format response
     * @return response lỗi phù hợp với từng API
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception, HttpServletRequest request) {
        log.warn(exception.getMessage(), exception);

        String path = request.getServletPath();
        String method = request.getMethod();

        if ("/login".equals(path)) {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "000");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(errors));
        }

        if ("/department".equals(path)) {
            return ResponseEntity.ok(DepartmentResponse.error("ER023", Collections.emptyList()));
        }

        if ("/certification".equals(path)) {
            return ResponseEntity.ok(CertificationResponse.error("ER023", Collections.emptyList()));
        }

        if ("/employee/validate".equals(path)) {
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }

        if ("/employee".equals(path) && "GET".equals(method)) {
            return ResponseEntity.ok(EmployeeListResponse.error("ER023", Collections.emptyList()));
        }

        if (path.startsWith("/employee/") && "GET".equals(method)) {
            return ResponseEntity.ok(EmployeeDetailResponse.error("ER023", Collections.emptyList()));
        }

        if ("/employee".equals(path) && "POST".equals(method)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EmployeeValidationResponse.error("ER015", Collections.emptyList()));
        }

        if ("/employee".equals(path) && "PUT".equals(method)) {
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }

        if ("/employee".equals(path) && "DELETE".equals(method)) {
            String employeeId = request.getParameter("employeeId");
            return ResponseEntity.ok(EmployeeDeleteResponse.error(employeeId, "ER023", Collections.emptyList()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
    }
}
