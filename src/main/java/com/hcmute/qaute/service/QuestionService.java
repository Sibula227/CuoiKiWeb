package com.hcmute.qaute.service;

import com.hcmute.qaute.dto.QuestionCreateDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import java.util.List;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionCreateDTO dto, String username);
    List<QuestionResponseDTO> getMyQuestions(String username);

    // Dành cho Dashboard (Phân quyền Admin/Advisor)
    List<QuestionResponseDTO> getQuestionsForDashboard(String username);

    // --- THÊM LẠI HÀM NÀY ---
    // Dành cho Trang chủ (Home) và Tìm kiếm (GraphQL) - Ai cũng xem được
    List<QuestionResponseDTO> getAllQuestions();
    // ------------------------

    QuestionResponseDTO getQuestionDetail(Long id);
}