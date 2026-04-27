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
 * Dùng để kiểm tra nhanh backend employee service đang hoạt động.
 */
@RestController
public class HomeController {

    /**
     * Trả về thông báo chào mừng khi truy cập endpoint gốc.
     *
     * @return chuỗi thông báo chào mừng service nhân viên
     */
    @RequestMapping("/")
    public String index() {
        return "Welcome to Employee service a";
    }
}
