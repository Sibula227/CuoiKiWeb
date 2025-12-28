// Dành cho Role ADMIN hoặc ADVISOR.
package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.AnswerDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.service.AnswerService;
import com.hcmute.qaute.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin") // Hoặc /advisor tùy config security
public class AdvisorController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;

    // Dashboard: Xem TẤT CẢ câu hỏi của sinh viên
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<QuestionResponseDTO> allQuestions = questionService.getAllQuestions();
        
        // Thống kê sơ bộ (Ví dụ)
        long countPending = allQuestions.stream().filter(q -> "PENDING".equals(q.getStatus().name())).count();
        
        model.addAttribute("questions", allQuestions);
        model.addAttribute("pendingCount", countPending);
        return "admin/dashboard"; // templates/admin/dashboard.html
    }

    // Xem chi tiết để trả lời
    @GetMapping("/question/{id}")
    public String viewQuestionToReply(@PathVariable Long id, Model model) {
        QuestionResponseDTO question = questionService.getQuestionDetail(id);
        List<AnswerDTO> answers = answerService.getAnswersByQuestionId(id);

        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
        return "admin/question_reply"; // templates/admin/question_reply.html
    }

    // Xử lý gửi câu trả lời
    @PostMapping("/question/{id}/reply")
    public String submitReply(@PathVariable Long id,
                              @RequestParam("content") String content,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        
        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nội dung trả lời không được để trống");
            return "redirect:/admin/question/" + id;
        }

        // Gọi service lưu câu trả lời (Service sẽ tự update status câu hỏi thành ANSWERED)
        answerService.addAnswer(id, content, authentication.getName());

        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi câu trả lời thành công!");
        return "redirect:/admin/dashboard";
    }
}