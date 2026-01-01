package com.hcmute.qaute.controller;

import com.hcmute.qaute.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    // 1. Trang nhập Email
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            userService.processForgotPassword(email);
            model.addAttribute("message", "Chúng tôi đã gửi link reset mật khẩu vào email của bạn. (Check Console nhé!)");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth/forgot_password";
    }

    // 2. Trang nhập Mật khẩu mới (Khi bấm vào link từ email)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // Kiểm tra token có hợp lệ không (logic đơn giản check null ở view hoặc service)
        model.addAttribute("token", token);
        return "auth/reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       Model model) {
        try {
            userService.updatePassword(token, password);
            model.addAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập.");
            return "auth/login"; // Chuyển về trang login
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/reset_password";
        }
    }
}