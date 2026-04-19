package com.luvina.la.controller;
/**
 * Bộ điều khiển chính cho trang chủ của ứng dụng.
 * Cung cấp endpoint gốc để kiểm tra trạng thái dịch vụ.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    /**
     * Endpoint gốc trả về thông báo chào mừng.
     *
     * @return Chuỗi thông báo chào mừng dịch vụ nhân viên
     */
    @RequestMapping("/")
    public String index() {
        return "Welcome to Employee service a";
    }

}
