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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        question.setStatus(QuestionStatus.PENDING);

        question.setStudentFaculty(dto.getFaculty());
        question.setStudentCohort(dto.getCohort());

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại!"));
            question.setDepartment(dept);
        }

        question.setCreatedAt(LocalDateTime.now());
        Question saved = questionRepository.save(question);
        return mapToDTO(saved);
    }

    @Override
    public List<QuestionResponseDTO> getMyQuestions(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Question> list = questionRepository.findByStudentId(student.getId());
        if (list != null) {
            list.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDTO> getQuestionsForDashboard(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Question> list = new ArrayList<>();
        String roleCode = currentUser.getRole().getCode();

        if ("ADMIN".equals(roleCode)) {
            list = questionRepository.findAll();
        } else if ("ADVISOR".equals(roleCode)) {
            if (currentUser.getDepartment() != null) {
                Long deptId = Long.valueOf(currentUser.getDepartment().getId());
                list = questionRepository.findByDepartmentId(deptId);
            } else {
                list = new ArrayList<>();
            }
        }

        if (list != null && !list.isEmpty()) {
            list.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
        }

        return list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDTO> getAllQuestions() {
        List<Question> list = questionRepository.findAll();
        if (list != null && !list.isEmpty()) {
            list.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public QuestionResponseDTO getQuestionDetail(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + id));
        return mapToDTO(q);
    }

    @Override
    public List<QuestionResponseDTO> searchQuestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Use an arbitrarily large page size or unpaged to get all results for now
        // Since the interface expects a List, we'll convert the Page result.
        org.springframework.data.domain.Page<Question> pageResult = questionRepository.searchByKeyword(keyword,
                org.springframework.data.domain.PageRequest.of(0, 50));

        List<Question> list = pageResult.getContent();
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // --- HÀM CHUYỂN ĐỔI ENTITY -> DTO (QUAN TRỌNG) ---
    private QuestionResponseDTO mapToDTO(Question q) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(q.getId());
        dto.setTitle(q.getTitle());
        dto.setContent(q.getContent());
        dto.setPriority(q.getPriority());
        dto.setStatus(q.getStatus());
        dto.setCreatedAt(q.getCreatedAt());
        dto.setTimeAgo(calculateTimeAgo(q.getCreatedAt()));

        dto.setStudentFaculty(q.getStudentFaculty());
        dto.setStudentCohort(q.getStudentCohort());

        // Mapping thông tin sinh viên
        if (q.getStudent() != null) {
            dto.setStudentId(q.getStudent().getId());
            dto.setStudentName(q.getStudent().getFullName());
            dto.setStudentAvatar(q.getStudent().getAvatarUrl());

            // --- [ĐÃ BỔ SUNG] Lấy MSSV thật để hiện lên Dashboard ---
            dto.setStudentIdCode(q.getStudent().getStudentIdCode());
            // --------------------------------------------------------
        }

        if (q.getDepartment() != null) {
            dto.setDepartmentId(q.getDepartment().getId());
            dto.setDepartmentName(q.getDepartment().getName());
        }

        return dto;
    }

    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null)
            return "Vừa xong";
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long seconds = duration.getSeconds();
        if (seconds < 60)
            return "Vừa xong";
        else if (seconds < 3600)
            return (seconds / 60) + " phút trước";
        else if (seconds < 86400)
            return (seconds / 3600) + " giờ trước";
        else
            return (seconds / 86400) + " ngày trước";
    }
}