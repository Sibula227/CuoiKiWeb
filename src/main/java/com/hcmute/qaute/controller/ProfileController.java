package com.hcmute.qaute.controller;

import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
            @org.springframework.web.bind.annotation.RequestParam("avatarFile") org.springframework.web.multipart.MultipartFile avatarFile,
            Authentication authentication,
            RedirectAttributes ra) {
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update allow fields
        currentUser.setFullName(updatedUser.getFullName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setStudentIdCode(updatedUser.getStudentIdCode());

        // Xử lý upload ảnh (Base64)
        if (!avatarFile.isEmpty()) {
            try {
                // Kiểm tra loại file (chỉ cho phép ảnh)
                String contentType = avatarFile.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    byte[] bytes = avatarFile.getBytes();
                    String base64Image = java.util.Base64.getEncoder().encodeToString(bytes);
                    // Lưu dạng Data URI: data:image/png;base64,.....
                    currentUser.setAvatarUrl("data:" + contentType + ";base64," + base64Image);
                } else {
                    ra.addFlashAttribute("errorMessage", "Vui lòng chỉ tải lên file ảnh!");
                    return "redirect:/profile";
                }
            } catch (java.io.IOException e) {
                ra.addFlashAttribute("errorMessage", "Lỗi khi xử lý ảnh tải lên.");
                return "redirect:/profile";
            }
        }

        try {
            userRepository.save(currentUser);
            ra.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}
