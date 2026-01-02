package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.AnswerDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import com.hcmute.qaute.service.AnswerService;
import com.hcmute.qaute.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
// Chỉ cho phép ADMIN hoặc ADVISOR truy cập
@PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')") 
public class AdminController {

    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private AnswerService answerService;

    // 1. DASHBOARD QUẢN LÝ
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) { // <-- THÊM Authentication
        
        // SỬA DÒNG NÀY: Gọi hàm mới và truyền username vào để lọc dữ liệu
        List<QuestionResponseDTO> allQuestions = questionService.getQuestionsForDashboard(authentication.getName());

        // Thống kê sơ bộ
        long pendingCount = allQuestions.stream()
                .filter(q -> q.getStatus() != null && q.getStatus() != QuestionStatus.ANSWERED)
                .count();
        long answeredCount = allQuestions.size() - pendingCount;

        model.addAttribute("questions", allQuestions);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("answeredCount", answeredCount);

        return "admin/dashboard"; 
    }

    // 2. XEM CHI TIẾT CÂU HỎI ĐỂ TRẢ LỜI
    @GetMapping("/question/{id}")
    public String viewQuestionToAnswer(@PathVariable Long id, Model model) {
        QuestionResponseDTO question = questionService.getQuestionDetail(id);
        List<AnswerDTO> answers = answerService.getAnswersByQuestionId(id);

        model.addAttribute("question", question);
        model.addAttribute("answers", answers);

        return "admin/question_detail"; 
    }

    // 3. XỬ LÝ TRẢ LỜI CÂU HỎI
    @PostMapping("/question/{id}/answer")
    public String submitAnswer(@PathVariable Long id,
                               @RequestParam("content") String content,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        
        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nội dung trả lời không được để trống!");
            return "redirect:/admin/question/" + id;
        }

        try {
            answerService.addAnswer(id, content, authentication.getName());
            
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi câu trả lời thành công!");
            return "redirect:/admin/question/" + id; 
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/question/" + id;
        }
    }
}