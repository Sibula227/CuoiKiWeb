package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Tìm Role bằng tên (ví dụ: "STUDENT", "ADMIN")
    Optional<Role> findByCode(String code);
}