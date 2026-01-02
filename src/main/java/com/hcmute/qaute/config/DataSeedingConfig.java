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
            admin.setRole(roleRepository.findByCode("ADMIN").orElse(null));
            admin.setIsActive(true); 
            userRepository.save(admin);
            System.out.println(">> Đã tạo Admin: admin / 123456");
        }

        // 4. Tạo Student mặc định (Pass: 123456)
        if (!userRepository.findByUsername("student").isPresent()) {
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@hcmute.edu.vn");
            student.setFullName("Nguyễn Văn A");
            student.setStudentIdCode("2011001");
            student.setPasswordHash(passwordEncoder.encode("123456"));
            student.setRole(roleRepository.findByCode("STUDENT").orElse(null));
            student.setIsActive(true);
            userRepository.save(student);
            System.out.println(">> Đã tạo Student: student / 123456");
        }

        // 5. [MỚI] TẠO 3 ADVISOR CHO 3 PHÒNG BAN (Pass: 123456)
        
        // 5.1 Advisor Phòng Đào Tạo
        if (!userRepository.findByUsername("advisor_dt").isPresent()) {
            User u = new User();
            u.setUsername("daotaoadvi");
            u.setEmail("dt@hcmute.edu.vn");
            u.setFullName("TVV Đào Tạo");
            u.setPasswordHash(passwordEncoder.encode("daotao123"));
            u.setRole(roleRepository.findByCode("ADVISOR").orElse(null));
            u.setIsActive(true);
            
            // Gán vào Phòng Đào Tạo
            u.setDepartment(departmentRepository.findByCode("DT").orElse(null)); 
            
            userRepository.save(u);
            System.out.println(">> Đã tạo Advisor Đào Tạo: advisor_dt / 123456");
        }

        // 5.2 Advisor Phòng CTSV
        if (!userRepository.findByUsername("advisor_ctsv").isPresent()) {
            User u = new User();
            u.setUsername("CTSVadvi");
            u.setEmail("ctsv@hcmute.edu.vn");
            u.setFullName("TVV CTSV");
            u.setPasswordHash(passwordEncoder.encode("CTSV123"));
            u.setRole(roleRepository.findByCode("ADVISOR").orElse(null));
            u.setIsActive(true);

            // Gán vào Phòng CTSV
            u.setDepartment(departmentRepository.findByCode("CTSV").orElse(null));

            userRepository.save(u);
            System.out.println(">> Đã tạo Advisor CTSV: advisor_ctsv / 123456");
        }

        // 5.3 Advisor Phòng Tài Chính
        if (!userRepository.findByUsername("advisor_tc").isPresent()) {
            User u = new User();
            u.setUsername("taichinhadvi");
            u.setEmail("tc@hcmute.edu.vn");
            u.setFullName("TVV Tài Chính");
            u.setPasswordHash(passwordEncoder.encode("taichinh123"));
            u.setRole(roleRepository.findByCode("ADVISOR").orElse(null));
            u.setIsActive(true);

            // Gán vào Phòng Tài Chính
            u.setDepartment(departmentRepository.findByCode("TC").orElse(null));

            userRepository.save(u);
            System.out.println(">> Đã tạo Advisor Tài Chính: advisor_tc / 123456");
        }
    }
}