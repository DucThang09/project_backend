package com.luvina.la.controller;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý endpoint kiểm tra trạng thái service.
 */
@RestController
public class HomeController {

    /**
     * Endpoint gốc trả về thông báo chào mừng.
     *
     * @return chuỗi thông báo chào mừng service nhân viên
     */
    @RequestMapping("/")
    public String index() {
        return "Welcome to Employee service a";
    }
}
