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

    // ======================================================
    // 1. DASHBOARD CÁ NHÂN
    // ======================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        try {
            // Kiểm tra authentication có tồn tại không
            if (authentication == null || authentication.getName() == null) {
                model.addAttribute("errorMessage", "Bạn cần đăng nhập để truy cập trang này.");
                return "redirect:/login";
            }
            
            // Lấy username người đang login
            String username = authentication.getName(); 
            
            // Gọi Service lấy danh sách câu hỏi của user này
            List<QuestionResponseDTO> myQuestions = questionService.getMyQuestions(username);
            
            // Đẩy dữ liệu sang View
            model.addAttribute("questions", myQuestions);
            
            // Trả về file HTML: src/main/resources/templates/student/dashboard.html
            // LƯU Ý: Thư mục phải là 'student' (không có s)
            return "student/dashboard"; 
        } catch (RuntimeException e) {
            // Xử lý lỗi nếu user không tồn tại hoặc có lỗi khác
            e.printStackTrace();
            model.addAttribute("errorMessage", "Lỗi khi tải dashboard: " + e.getMessage());
            return "redirect:/login?error";
        } catch (Exception e) {
            // Xử lý các lỗi khác
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
            return "redirect:/login?error";
        }
    }

    // ======================================================
    // 2. TẠO CÂU HỎI MỚI
    // ======================================================
    @GetMapping("/create-question")
    public String createQuestionForm(Model model) {
        model.addAttribute("questionDTO", new QuestionCreateDTO());
        // Lấy danh sách phòng ban để hiển thị trong <select>
        model.addAttribute("departments", departmentService.getAllDepartments());
        
        return "student/create_question"; // Cần tạo file này
    }

    @PostMapping("/create-question")
    public String saveQuestion(@Valid @ModelAttribute("questionDTO") QuestionCreateDTO dto,
                               BindingResult result,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // 1. Nếu form nhập sai (bỏ trống...)
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "student/create_question";
        }

        try {
            // 2. Gọi Service lưu vào DB
            questionService.createQuestion(dto, authentication.getName());
            
            // 3. Thông báo thành công và quay về Dashboard
            redirectAttributes.addFlashAttribute("successMessage", "Gửi câu hỏi thành công! Vui lòng chờ phản hồi.");
            return "redirect:/student/dashboard";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "student/create_question";
        }
    }

    // ======================================================
    // 3. XEM CHI TIẾT & PHẢN HỒI
    // ======================================================
    @GetMapping("/question/{id}")
    public String viewQuestionDetail(@PathVariable Long id, 
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin câu hỏi
            QuestionResponseDTO question = questionService.getQuestionDetail(id);
            // Lấy các câu trả lời
            List<AnswerDTO> answers = answerService.getAnswersByQuestionId(id);

            model.addAttribute("question", question);
            model.addAttribute("answers", answers);
            
            return "student/question_detail"; // Cần tạo file này
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy câu hỏi!");
            return "redirect:/student/dashboard";
        }
    }
    
    @PostMapping("/question/{id}/reply")
    public String studentReply(@PathVariable Long id, 
                               @RequestParam("content") String content,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        
        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nội dung không được để trống");
            return "redirect:/student/question/" + id;
        }

        answerService.addAnswer(id, content, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi phản hồi.");
        return "redirect:/student/question/" + id;
    }
}