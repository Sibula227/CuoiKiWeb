package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.dto.UserDTO;
import com.hcmute.qaute.dto.UserRegisterDTO;
import com.hcmute.qaute.entity.Role;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.repository.RoleRepository;
import com.hcmute.qaute.repository.UserRepository;
import com.hcmute.qaute.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // Cần import để xử lý thời gian
import java.util.UUID; // Cần import để tạo Token ngẫu nhiên

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setStudentIdCode(dto.getStudentIdCode());

        // Gán quyền mặc định là STUDENT
        Role studentRole = roleRepository.findByCode("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role STUDENT không tìm thấy trong DB"));
        user.setRole(studentRole);

        // Kích hoạt tài khoản ngay (hoặc false nếu bắt xác thực email)
        user.setIsActive(true);

        return userRepository.save(user);
    }

    @Override
    public UserDTO findByUsername(String username) {
        User user = getUserEntity(username);
        return mapToDTO(user);
    }

    @Override
    public User getUserEntity(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy: " + username));
    }

    // --- PHẦN BỔ SUNG: XỬ LÝ QUÊN MẬT KHẨU ---

    @Override
    public String processForgotPassword(String email) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email này chưa được đăng ký trong hệ thống!"));

        // 2. Tạo Token ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // 3. Lưu Token và thời gian hết hạn (ví dụ: 15 phút) vào DB
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // 4. Giả lập gửi Email (In link ra Console để bạn click test)
        // Trong thực tế, đoạn này sẽ gọi JavaMailSender
        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        System.out.println("==================================================");
        System.out.println("[EMAIL MOCK] Gửi link reset password tới: " + email);
        System.out.println("LINK: " + resetLink);
        System.out.println("==================================================");

        return token;
    }

    @Override
    public void updatePassword(String token, String newPassword) {
        // 1. Tìm user theo token
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc không tồn tại!"));

        // 2. Kiểm tra token có hết hạn chưa
        if (user.getResetPasswordTokenExpiry() == null ||
                user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link reset mật khẩu đã hết hạn! Vui lòng thử lại.");
        }

        // 3. Mã hóa mật khẩu mới và lưu vào DB
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        // 4. Xóa token sau khi đã dùng xong (để không dùng lại được nữa)
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
    }

    // ------------------------------------------

    // Manual Mapping Entity -> DTO
    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setStudentIdCode(user.getStudentIdCode());
        dto.setPhone(user.getPhone());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getCode());
        }
        return dto;
    }
}