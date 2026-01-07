-- 1. Tạo Roles
INSERT INTO roles (code, display_name, description, created_at) VALUES 
('ADMIN', 'Administrator', 'Quản trị viên', NOW()),
('ADVISOR', 'Advisor', 'Tư vấn viên', NOW()),
('STUDENT', 'Student', 'Sinh viên', NOW());

-- 2. Tạo Departments
INSERT INTO departments (code, name, description, created_at) VALUES 
('DT', 'Phòng Đào Tạo', 'Xử lý học vụ', NOW()),
('CTSV', 'Phòng CTSV', 'Xử lý điểm rèn luyện', NOW()),
('TC', 'Phòng Tài Chính', 'Học phí', NOW());

-- 3. Tạo Admin (Password: 123456 - Hash placeholder)
INSERT INTO users (username, email, full_name, password_hash, role_id, is_active, created_at, updated_at) 
SELECT 'admin', 'admin@hcmute.edu.vn', 'Quản Trị Viên', '$2a$10$DUMMYHASHFOR12345678901234567890123456789012345678901', id, 1, NOW(), NOW()
FROM roles WHERE code = 'ADMIN';

-- 4. Tạo Student (Password: 123456 - Hash placeholder)
INSERT INTO users (username, email, full_name, student_id, password_hash, role_id, is_active, created_at, updated_at) 
SELECT 'student', 'student@hcmute.edu.vn', 'Nguyễn Văn A', '2011001', '$2a$10$DUMMYHASHFOR12345678901234567890123456789012345678901', id, 1, NOW(), NOW()
FROM roles WHERE code = 'STUDENT';

-- 5. Tạo Advisors
-- 5.1 Advisor Phòng Đào Tạo (user: daotaoadvi)
INSERT INTO users (username, email, full_name, password_hash, role_id, department_id, is_active, created_at, updated_at)
SELECT 'daotaoadvi', 'dt@hcmute.edu.vn', 'TVV Đào Tạo', '$2a$10$DUMMYHASHFORDAOTAO123456789012345678901234567890123', r.id, d.id, 1, NOW(), NOW()
FROM roles r, departments d WHERE r.code = 'ADVISOR' AND d.code = 'DT';

-- 5.2 Advisor Phòng CTSV (user: CTSVadvi)
INSERT INTO users (username, email, full_name, password_hash, role_id, department_id, is_active, created_at, updated_at)
SELECT 'CTSVadvi', 'ctsv@hcmute.edu.vn', 'TVV CTSV', '$2a$10$DUMMYHASHFORCTSV123456789012345678901234567890123456', r.id, d.id, 1, NOW(), NOW()
FROM roles r, departments d WHERE r.code = 'ADVISOR' AND d.code = 'CTSV';

-- 5.3 Advisor Phòng Tài Chính (user: taichinhadvi)
INSERT INTO users (username, email, full_name, password_hash, role_id, department_id, is_active, created_at, updated_at)
SELECT 'taichinhadvi', 'tc@hcmute.edu.vn', 'TVV Tài Chính', '$2a$10$DUMMYHASHFORTAICHINH12345678901234567890123456789012', r.id, d.id, 1, NOW(), NOW()
FROM roles r, departments d WHERE r.code = 'ADVISOR' AND d.code = 'TC';
