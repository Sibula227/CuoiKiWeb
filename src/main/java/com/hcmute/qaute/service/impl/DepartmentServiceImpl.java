package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.dto.DepartmentDTO;
import com.hcmute.qaute.entity.Department;
import com.hcmute.qaute.repository.DepartmentRepository;
import com.hcmute.qaute.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private DepartmentDTO mapToDTO(Department dept) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(dept.getId());
        dto.setCode(dept.getCode());
        dto.setName(dept.getName());
        dto.setDescription(dept.getDescription());
        return dto;
    }
}