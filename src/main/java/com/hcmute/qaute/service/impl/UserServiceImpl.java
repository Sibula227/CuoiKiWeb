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
        
        // Mặc định đăng ký là STUDENT. Nếu muốn tạo Admin thì phải sửa trong Database
        Role studentRole = roleRepository.findByCode("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role STUDENT không tìm thấy trong DB"));
        user.setRole(studentRole);

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