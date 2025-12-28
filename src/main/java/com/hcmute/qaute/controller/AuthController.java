//Controller này xử lý các trang không cần đăng nhập.
package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.UserRegisterDTO;
import com.hcmute.qaute.service.UserService;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication; // <--- QUAN TRỌNG NHẤT
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Trang chủ: Nếu chưa đăng nhập thì hiện trang giới thiệu hoặc login
    @GetMapping("/")
    public String index() {
        return "redirect:/login"; // Redirect thẳng về login cho đơn giản
    }

    // Trang Login (Spring Security sẽ tự xử lý POST, mình chỉ cần trả về View GET)
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }
        return "auth/login"; // File: templates/auth/login.html
    }

    // Trang Đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDTO", new UserRegisterDTO());
        return "auth/register"; // File: templates/auth/register.html
    }

    // Xử lý hành động Đăng ký
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("userDTO") UserRegisterDTO userDTO,
                                  BindingResult bindingResult,
                                  Model model) {
        // 1. Check validate form (rỗng, sai email...)
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // 2. Gọi Service đăng ký
        try {
            userService.registerUser(userDTO);
            return "redirect:/login?registerSuccess"; // Chuyển về login báo thành công
        } catch (RuntimeException e) {
            // Lỗi nghiệp vụ (trùng email, trùng username)
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        // Lấy role của user đang login
        String role = authentication.getAuthorities().stream()
                .findFirst().get().getAuthority();

        if (role.equals("ADMIN") || role.equals("ADVISOR")) {
            return "redirect:/admin/dashboard";
        } else if (role.equals("STUDENT")) {
            return "redirect:/student/dashboard";
        }
        return "redirect:/";
    }
}