// Xử lý tạo câu hỏi, lấy danh sách, xem chi tiết.
package com.hcmute.qaute.service;

import com.hcmute.qaute.dto.QuestionCreateDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import java.util.List;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionCreateDTO dto, String username);
    List<QuestionResponseDTO> getMyQuestions(String username);
    List<QuestionResponseDTO> getAllQuestions(); // Cho Admin
    QuestionResponseDTO getQuestionDetail(Long id);
}