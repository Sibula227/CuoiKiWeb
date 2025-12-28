package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Dùng cho Login (Spring Security loadByUsername)
    Optional<User> findByUsername(String username);

    // Kiểm tra email đã tồn tại chưa khi đăng ký
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // Kiểm tra MSSV đã tồn tại chưa
    boolean existsByStudentIdCode(String studentIdCode);
}