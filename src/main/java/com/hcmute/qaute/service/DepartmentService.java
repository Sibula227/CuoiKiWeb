// Đơn giản là lấy danh sách để hiển thị lên dropdown.
package com.hcmute.qaute.service;

import com.hcmute.qaute.dto.DepartmentDTO;
import java.util.List;

public interface DepartmentService {
    List<DepartmentDTO> getAllDepartments();
    }