// Admin/Advisor trả lời câu hỏi -> Đổi trạng thái câu hỏi thành ANSWERED.
package com.hcmute.qaute.service;

import com.hcmute.qaute.dto.AnswerDTO;
import java.util.List; 

public interface AnswerService {
    // Phương thức thêm câu trả lời
    AnswerDTO addAnswer(Long questionId, String content, String username);

    // Phương thức lấy danh sách câu trả lời 
    List<AnswerDTO> getAnswersByQuestionId(Long questionId);
}