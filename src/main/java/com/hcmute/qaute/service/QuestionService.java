package com.hcmute.qaute.service;

import com.hcmute.qaute.dto.QuestionCreateDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import java.util.List;

public interface QuestionService {
    // QuestionResponseDTO createQuestion(QuestionCreateDTO dto, String username);

    List<QuestionResponseDTO> getMyQuestions(String username);

    // Dành cho Dashboard (Phân quyền Admin/Advisor)
    List<QuestionResponseDTO> getQuestionsForDashboard(String username);

    // --- THÊM LẠI HÀM NÀY ---
    // Dành cho Trang chủ (Home) và Tìm kiếm (GraphQL) - Ai cũng xem được
    List<QuestionResponseDTO> getAllQuestions();
    // ------------------------

    // Updated createQuestion to handle Tags and Files
    QuestionResponseDTO createQuestion(QuestionCreateDTO dto, String username, String tagInput,
            org.springframework.web.multipart.MultipartFile[] files);

    QuestionResponseDTO getQuestionDetail(Long id);

    // Tìm kiếm câu hỏi
    List<QuestionResponseDTO> searchQuestions(String keyword);

    // Filter updated
    List<QuestionResponseDTO> getFilterQuestions(Integer departmentId, String sort, String tag);
}