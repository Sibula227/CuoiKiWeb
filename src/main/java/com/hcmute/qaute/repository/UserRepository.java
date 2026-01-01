package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Dùng cho Login
    Optional<User> findByUsername(String username);

    // Dùng cho Đăng ký & Gửi mail quên mật khẩu
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // Kiểm tra MSSV
    boolean existsByStudentIdCode(String studentIdCode);

    // --- BỔ SUNG DÒNG NÀY ---
    // Tìm user sở hữu token này để cho phép đổi mật khẩu
    Optional<User> findByResetPasswordToken(String token);
}