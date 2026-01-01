// Xử lý đăng ký, lấy thông tin người dùng.
package com.hcmute.qaute.service;

import com.hcmute.qaute.dto.UserDTO;
import com.hcmute.qaute.dto.UserRegisterDTO;
import com.hcmute.qaute.entity.User;

public interface UserService {
    User registerUser(UserRegisterDTO registerDTO);
    UserDTO findByUsername(String username);
    User getUserEntity(String username); // Hàm tiện ích dùng nội bộ
    void processForgotPassword(String email);
    void updatePassword(String token, String newPassword);
}
