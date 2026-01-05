package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.UserRegisterDTO;
import com.hcmute.qaute.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority; // Import thêm cái này để xử lý role
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private com.hcmute.qaute.service.AuditLogService auditLogService;

    // --- ĐÃ XÓA METHOD index() map với "/" để tránh xung đột với HomeController
    // ---

    // Trang Login
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "accessDenied", required = false) String accessDenied,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }
        if (accessDenied != null) {
            model.addAttribute("errorMessage",
                    "Bạn không có quyền truy cập trang này. Vui lòng kiểm tra lại role của tài khoản.");
        }
        return "auth/login";
    }

    // Trang Đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDTO", new UserRegisterDTO());
        return "auth/register";
    }

    // Xử lý hành động Đăng ký
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("userDTO") UserRegisterDTO userDTO,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.registerUser(userDTO);
            auditLogService.log(userDTO.getUsername(), "REGISTER", "User", null, "User registered successfully");
            return "redirect:/login?registerSuccess";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    // Xử lý điều hướng sau khi đăng nhập thành công
    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        String username = authentication.getName();
        auditLogService.log(username, "LOGIN", "User", null, "User logged in successfully");

        // Lấy role của user đang login (Stream để lấy role đầu tiên trong list)
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        if (role.equals("ADMIN") || role.equals("ADVISOR")) {
            return "redirect:/admin/dashboard";
        } else if (role.equals("STUDENT")) {
            // Thay vì về dashboard, ta có thể cho về trang chủ để xem diễn đàn
            // return "redirect:/student/dashboard";
            return "redirect:/"; // Về lại trang chủ (Home) sau khi login cho tự nhiên
        }
        return "redirect:/";
    }
}