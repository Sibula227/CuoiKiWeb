// Bắt buộc phải có Role STUDENT mới vào được (Cấu hình ở SecurityConfig).
package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.AnswerDTO;
import com.hcmute.qaute.dto.QuestionCreateDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.service.AnswerService;
import com.hcmute.qaute.service.DepartmentService;
import com.hcmute.qaute.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private AnswerService answerService;

    // Dashboard: Xem danh sách câu hỏi CỦA MÌNH
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName(); // Lấy username người đang login
        List<QuestionResponseDTO> myQuestions = questionService.getMyQuestions(username);
        
        model.addAttribute("questions", myQuestions);
        model.addAttribute("username", username);
        return "student/dashboard"; // templates/student/dashboard.html
    }

    // Trang tạo câu hỏi mới
    @GetMapping("/create-question")
    public String createQuestionForm(Model model) {
        model.addAttribute("questionDTO", new QuestionCreateDTO());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "student/create_question"; // templates/student/create_question.html
    }

    // Xử lý lưu câu hỏi
    @PostMapping("/create-question")
    public String saveQuestion(@Valid @ModelAttribute("questionDTO") QuestionCreateDTO dto,
                               BindingResult result,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "student/create_question";
        }

        questionService.createQuestion(dto, authentication.getName());
        
        // Dùng RedirectAttributes để hiển thị thông báo sau khi chuyển trang
        redirectAttributes.addFlashAttribute("successMessage", "Gửi câu hỏi thành công! Vui lòng chờ phản hồi.");
        return "redirect:/student/dashboard";
    }

    // Xem chi tiết câu hỏi & Xem câu trả lời
    @GetMapping("/question/{id}")
    public String viewQuestionDetail(@PathVariable Long id, Model model) {
        // 1. Lấy thông tin câu hỏi
        QuestionResponseDTO question = questionService.getQuestionDetail(id);
        
        // 2. Lấy danh sách câu trả lời của câu hỏi đó
        List<AnswerDTO> answers = answerService.getAnswersByQuestionId(id);

        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
        
        // Sinh viên có thể comment lại (reply) vào chính câu hỏi của mình
        // Ta dùng chung form addAnswer nhưng logic xử lý ở Controller khác hoặc chung
        return "student/question_detail"; // templates/student/question_detail.html
    }
    
    // Sinh viên phản hồi lại (Comment tiếp)
    @PostMapping("/question/{id}/reply")
    public String studentReply(@PathVariable Long id, 
                               @RequestParam("content") String content,
                               Authentication authentication) {
        answerService.addAnswer(id, content, authentication.getName());
        return "redirect:/student/question/" + id;
    }
}
