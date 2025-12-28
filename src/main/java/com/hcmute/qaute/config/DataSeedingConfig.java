package com.hcmute.qaute.config;

import com.hcmute.qaute.entity.Department;
import com.hcmute.qaute.entity.Role;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.repository.DepartmentRepository;
import com.hcmute.qaute.repository.RoleRepository;
import com.hcmute.qaute.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeedingConfig implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Tạo Roles nếu chưa có
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ADMIN", "Administrator", "Quản trị viên", null));
            roleRepository.save(new Role(null, "ADVISOR", "Advisor", "Tư vấn viên", null));
            roleRepository.save(new Role(null, "STUDENT", "Student", "Sinh viên", null));
        }

        // 2. Tạo Departments nếu chưa có
        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department(null, "DT", "Phòng Đào Tạo", "Xử lý học vụ", null));
            departmentRepository.save(new Department(null, "CTSV", "Phòng CTSV", "Xử lý điểm rèn luyện", null));
            departmentRepository.save(new Department(null, "TC", "Phòng Tài Chính", "Học phí", null));
        }

        // 3. Tạo Admin mặc định (Pass: 123456)
        if (!userRepository.findByUsername("admin").isPresent()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@hcmute.edu.vn");
            admin.setFullName("Quản Trị Viên");
            admin.setPasswordHash(passwordEncoder.encode("123456"));
            admin.setRole(roleRepository.findByCode("ADMIN").get());
            userRepository.save(admin);
        }

        // 4. Tạo Student mặc định (Pass: 123456)
        if (!userRepository.findByUsername("student").isPresent()) {
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@hcmute.edu.vn");
            student.setFullName("Nguyễn Văn A");
            student.setStudentIdCode("2011001");
            student.setPasswordHash(passwordEncoder.encode("123456"));
            student.setRole(roleRepository.findByCode("STUDENT").get());
            userRepository.save(student);
        }
    }
}