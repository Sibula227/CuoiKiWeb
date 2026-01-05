package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.dto.AnswerDTO;
import com.hcmute.qaute.entity.Answer;
import com.hcmute.qaute.entity.Question;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import com.hcmute.qaute.repository.AnswerRepository;
import com.hcmute.qaute.repository.QuestionRepository;
import com.hcmute.qaute.repository.UserRepository;
import com.hcmute.qaute.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private com.hcmute.qaute.service.NotificationService notificationService;

    @Override
    public AnswerDTO addAnswer(Long questionId, String content, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Answer answer = new Answer();
        answer.setContent(content);

        // SỬA 1: Dùng setAuthor thay vì setUser (cho khớp với Entity Answer)
        answer.setAuthor(user);

        answer.setQuestion(question);
        // createdAt sẽ tự động được tạo bởi @PrePersist trong Entity, nhưng gán tay
        // cũng không sao
        // answer.setCreatedAt(LocalDateTime.now());

        Answer savedAnswer = answerRepository.save(answer);

        // Logic: Nếu Admin/Advisor trả lời -> Cập nhật trạng thái câu hỏi
        if (user.getRole().getCode().equals("ADMIN") || user.getRole().getCode().equals("ADVISOR")) {
            question.setStatus(QuestionStatus.ANSWERED);
            questionRepository.save(question);

            // --- THÔNG BÁO CHO SINH VIÊN ---
            // Chỉ thông báo nếu người trả lời KHÁC người hỏi
            if (!user.getId().equals(question.getStudent().getId())) {
                notificationService.notifyUser(
                        question.getStudent(),
                        "Câu hỏi của bạn đã được trả lời!",
                        "Cố vấn " + user.getFullName() + " vừa trả lời câu hỏi: " + question.getTitle(),
                        "NEW_ANSWER",
                        savedAnswer.getId().toString());
            }
            // -------------------------------
        }

        // SỬA 2: Trả về DTO thay vì void (cho khớp với Interface)
        return mapToDTO(savedAnswer);
    }

    @Override
    public List<AnswerDTO> getAnswersByQuestionId(Long questionId) {
        // SỬA DÒNG NÀY: Gọi đúng tên hàm có trong Repository
        List<Answer> answers = answerRepository.findByQuestionIdOrderByCreatedAtAsc(questionId);

        return answers.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Hàm phụ trợ MAP Entity -> DTO ---
    private AnswerDTO mapToDTO(Answer ans) {
        AnswerDTO dto = new AnswerDTO();
        dto.setId(ans.getId());
        dto.setQuestionId(ans.getQuestion().getId());
        dto.setContent(ans.getContent());
        dto.setCreatedAt(ans.getCreatedAt());

        // --- MAP DỮ LIỆU USER (Dùng getAuthor thay vì getUser) ---
        if (ans.getAuthor() != null) {
            // Map vào biến 'username'
            dto.setUsername(ans.getAuthor().getFullName());

            // Map vào biến 'avatarUrl'
            dto.setAvatarUrl(ans.getAuthor().getAvatarUrl());

            // Map vào biến 'roleCode'
            if (ans.getAuthor().getRole() != null) {
                dto.setRoleCode(ans.getAuthor().getRole().getCode());
            } else {
                dto.setRoleCode("STUDENT");
            }
        } else {
            dto.setUsername("Ẩn danh");
            dto.setRoleCode("STUDENT");
        }

        // --- TÍNH TOÁN THỜI GIAN ---
        dto.setTimeAgo(calculateTimeAgo(ans.getCreatedAt()));

        return dto;
    }

    // Hàm tính thời gian "X phút trước"
    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null)
            return "Vừa xong";
        long seconds = Duration.between(createdAt, LocalDateTime.now()).getSeconds();
        if (seconds < 60)
            return "Vừa xong";
        if (seconds < 3600)
            return (seconds / 60) + " phút trước";
        if (seconds < 86400)
            return (seconds / 3600) + " giờ trước";
        return (seconds / 86400) + " ngày trước";
    }
}