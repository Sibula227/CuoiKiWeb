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
                .orElseThrow(() -> new RuntimeException("User not found"));

        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setStudent(student);
        question.setStatus(QuestionStatus.PENDING); // Mặc định là Chờ xử lý

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
            question.setDepartment(dept);
        }

        Question saved = questionRepository.save(question);
        return mapToDTO(saved);
    }

    @Override
    public List<QuestionResponseDTO> getMyQuestions(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Question> list = questionRepository.findByStudentId(student.getId());
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponseDTO getQuestionDetail(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + id));
        return mapToDTO(q);
    }

    // Manual Mapping từ Entity sang DTO để trả về Frontend
    private QuestionResponseDTO mapToDTO(Question q) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(q.getId());
        dto.setTitle(q.getTitle());
        dto.setContent(q.getContent());
        dto.setPriority(q.getPriority());
        dto.setStatus(q.getStatus());
        dto.setCreatedAt(q.getCreatedAt());
        
        // Tính toán timeAgo đơn giản (Ví dụ: convert sang String)
        dto.setTimeAgo(q.getCreatedAt().toString()); 

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
}