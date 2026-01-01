package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.dto.QuestionCreateDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.entity.Department;
import com.hcmute.qaute.entity.Question;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import com.hcmute.qaute.repository.DepartmentRepository;
import com.hcmute.qaute.repository.QuestionRepository;
import com.hcmute.qaute.repository.UserRepository;
import com.hcmute.qaute.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration; // Import để tính khoảng thời gian
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public QuestionResponseDTO createQuestion(QuestionCreateDTO dto, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setStudent(student);
        question.setStatus(QuestionStatus.PENDING); // Mặc định là Chờ xử lý

        // --- CẬP NHẬT MỚI: LƯU KHOA VÀ KHÓA ---
        question.setStudentFaculty(dto.getFaculty());
        question.setStudentCohort(dto.getCohort());
        // --------------------------------------

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại!"));
            question.setDepartment(dept);
        }

        // Tự động gán thời gian tạo là hiện tại
        question.setCreatedAt(LocalDateTime.now());

        Question saved = questionRepository.save(question);
        return mapToDTO(saved);
    }

    @Override
    public List<QuestionResponseDTO> getMyQuestions(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Lấy danh sách câu hỏi và sắp xếp cái mới nhất lên đầu (giả sử DB chưa sort)
        List<Question> list = questionRepository.findByStudentId(student.getId());
        
        // Nên sort giảm dần theo ngày tạo để user thấy câu mới nhất
        list.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));

        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDTO> getAllQuestions() {
        List<Question> list = questionRepository.findAll();
        // Sort mới nhất lên đầu
        list.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
        
        return list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponseDTO getQuestionDetail(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + id));
        return mapToDTO(q);
    }
    
    // --- Manual Mapping Entity -> DTO ---
    private QuestionResponseDTO mapToDTO(Question q) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(q.getId());
        dto.setTitle(q.getTitle());
        dto.setContent(q.getContent());
        dto.setPriority(q.getPriority());
        dto.setStatus(q.getStatus());
        dto.setCreatedAt(q.getCreatedAt());
        
        // Cải tiến: Tính thời gian tương đối (VD: "2 giờ trước")
        dto.setTimeAgo(calculateTimeAgo(q.getCreatedAt())); 

        if (q.getStudent() != null) {
            dto.setStudentId(q.getStudent().getId());
            dto.setStudentName(q.getStudent().getFullName());
            dto.setStudentAvatar(q.getStudent().getAvatarUrl());
        }

        if (q.getDepartment() != null) {
            dto.setDepartmentId(q.getDepartment().getId());
            dto.setDepartmentName(q.getDepartment().getName());
        }
        
        return dto;
    }

    // Hàm phụ trợ tính thời gian "X phút trước"
    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "Vừa xong";
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "Vừa xong";
        } else if (seconds < 3600) {
            return (seconds / 60) + " phút trước";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " giờ trước";
        } else {
            return (seconds / 86400) + " ngày trước";
        }
    }
}