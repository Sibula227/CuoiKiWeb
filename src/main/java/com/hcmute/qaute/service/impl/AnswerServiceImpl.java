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
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional // Để đảm bảo cả 2 hành động (Lưu trả lời + Update status) thành công hoặc cùng thất bại
    public AnswerDTO addAnswer(Long questionId, String content, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // 1. Lưu câu trả lời
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(question);
        answer.setAuthor(author);
        answer.setIsPublic(true); // Mặc định public
        
        Answer savedAnswer = answerRepository.save(answer);

        // 2. Cập nhật trạng thái câu hỏi -> ANSWERED
        // Logic: Nếu người trả lời là ADMIN/ADVISOR thì mới set ANSWERED
        // Nếu sinh viên comment lại thì set thành PENDING hoặc REOPENED (Tùy logic bạn muốn)
        String roleCode = author.getRole().getCode();
        if ("ADMIN".equals(roleCode) || "ADVISOR".equals(roleCode)) {
            question.setStatus(QuestionStatus.ANSWERED);
            questionRepository.save(question);
        }

        return mapToDTO(savedAnswer);
    }

    @Override
    public List<AnswerDTO> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionIdOrderByCreatedAtAsc(questionId);
        return answers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private AnswerDTO mapToDTO(Answer answer) {
        AnswerDTO dto = new AnswerDTO();
        dto.setId(answer.getId());
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setContent(answer.getContent());
        dto.setCreatedAt(answer.getCreatedAt());

        if (answer.getAuthor() != null) {
            dto.setAuthorName(answer.getAuthor().getFullName());
            dto.setAuthorAvatar(answer.getAuthor().getAvatarUrl());
            if (answer.getAuthor().getRole() != null) {
                dto.setAuthorRole(answer.getAuthor().getRole().getCode());
            }
        }
        return dto;
    }
}