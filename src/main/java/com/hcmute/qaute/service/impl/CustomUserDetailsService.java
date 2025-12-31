package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service // Đánh dấu để Spring biết đây là 1 Service cần quản lý
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm user trong Database bằng UserRepository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        // 2. Lấy Role của user ra (Ví dụ: "STUDENT", "ADMIN")
        // Spring Security yêu cầu Role phải được bọc trong object GrantedAuthority
        String roleCode = user.getRole() != null ? user.getRole().getCode().trim().toUpperCase() : "STUDENT"; 
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(roleCode));

        // 3. Trả về đối tượng User chuẩn của Spring Security (Không phải User entity của bạn)
        // Đây là bước "phiên dịch" từ User của bạn sang User mà Spring hiểu
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(), // Spring sẽ lấy cái này so sánh với pass người dùng nhập
                user.getIsActive(),     // enabled (Tài khoản có kích hoạt không?)
                true,                   // accountNonExpired
                true,                   // credentialsNonExpired
                true,                   // accountNonLocked
                authorities             // Danh sách quyền
        );
    }
}