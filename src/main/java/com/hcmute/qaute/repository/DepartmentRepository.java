package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <--- Nhớ thêm import này

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    
    // SỬA: Thêm Optional bao quanh Department
    Optional<Department> findByCode(String code);
}