package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.AnswerDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.entity.Department;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import com.hcmute.qaute.repository.DepartmentRepository;
import com.hcmute.qaute.repository.RoleRepository;
import com.hcmute.qaute.repository.UserRepository;
import com.hcmute.qaute.service.AnswerService;
import com.hcmute.qaute.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
public class AdminController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private com.hcmute.qaute.service.AuditLogService auditLogService;

    // 1. DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        List<QuestionResponseDTO> allQuestions = questionService.getQuestionsForDashboard(authentication.getName());
        long pendingCount = allQuestions.stream()
                .filter(q -> q.getStatus() != null && q.getStatus() != QuestionStatus.ANSWERED).count();
        long answeredCount = allQuestions.size() - pendingCount;
        model.addAttribute("questions", allQuestions);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("answeredCount", answeredCount);
        return "admin/dashboard";
    }

    // 2. LIST USERS
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    // --- 3. CÁC HÀM CRUD USER ---

    // 3.1 Hiển thị Form Thêm mới
    @GetMapping("/users/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/user_form";
    }

    // 3.2 Hiển thị Form Edit
    @GetMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showEditUserForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            ra.addFlashAttribute("errorMessage", "User không tồn tại!");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/user_form";
    }

    // 3.3 Lưu User (Create & Edit)
    @PostMapping("/users/save")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes ra) {
        if (user.getId() == null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            user.setIsActive(true);
        } else {
            User existingUser = userRepository.findById(user.getId()).orElse(null);
            if (existingUser != null) {
                if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
                    user.setPasswordHash(existingUser.getPasswordHash());
                } else {
                    user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
                }
                user.setIsActive(existingUser.getIsActive());
                user.setCreatedAt(existingUser.getCreatedAt());
            }
        }
        userRepository.save(user);
        ra.addFlashAttribute("successMessage", "Lưu người dùng thành công!");
        return "redirect:/admin/users";
    }

    // --- ĐÃ XÓA HÀM DELETE USER CŨ TẠI ĐÂY ĐỂ TRÁNH LỖI ---

    // 4. CÁC HÀM XỬ LÝ CÂU HỎI
    @GetMapping("/question/{id}")
    public String viewQuestionToAnswer(@PathVariable Long id, Model model) {
        QuestionResponseDTO question = questionService.getQuestionDetail(id);
        List<AnswerDTO> answers = answerService.getAnswersByQuestionId(id);
        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
        return "admin/question_detail";
    }

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

    // 5. QUẢN LÝ PHÒNG BAN

    // 5.1 Danh sách
    @GetMapping("/departments")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageDepartments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/departments";
    }

    // 5.2 Form Thêm
    @GetMapping("/departments/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showCreateDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "admin/department_form";
    }

    // 5.3 Form Edit
    @GetMapping("/departments/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showEditDepartmentForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Department dept = departmentRepository.findById(id).orElse(null);
        if (dept == null) {
            ra.addFlashAttribute("errorMessage", "Phòng ban không tồn tại!");
            return "redirect:/admin/departments";
        }
        model.addAttribute("department", dept);
        return "admin/department_form";
    }

    // 5.4 Lưu
    @PostMapping("/departments/save")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String saveDepartment(@ModelAttribute("department") Department department, RedirectAttributes ra) {
        try {
            departmentRepository.save(department);
            ra.addFlashAttribute("successMessage", "Lưu phòng ban thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: Không thể lưu (có thể trùng mã phòng ban).");
        }
        return "redirect:/admin/departments";
    }

    // 5.5 Xóa Phòng ban
    @GetMapping("/departments/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteDepartment(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            departmentRepository.deleteById(id);
            ra.addFlashAttribute("successMessage", "Đã xóa phòng ban!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa phòng ban này vì đang có dữ liệu liên quan!");
        }
        return "redirect:/admin/departments";
    }

    // 6. XÓA USER (PHIÊN BẢN MỚI - BẢO MẬT CAO)
    // Đây là hàm duy nhất xử lý xóa user
    @GetMapping("/users/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteUser(@PathVariable Long id,
            RedirectAttributes ra,
            Authentication authentication) {

        User userToDelete = userRepository.findById(id).orElse(null);

        if (userToDelete == null) {
            ra.addFlashAttribute("errorMessage", "Người dùng không tồn tại!");
            return "redirect:/admin/users";
        }

        // DEFENSE 1: Chặn tự xóa
        String currentUsername = authentication.getName();
        if (userToDelete.getUsername().equals(currentUsername)) {
            ra.addFlashAttribute("errorMessage", "Bạn không thể tự xóa tài khoản của chính mình!");
            return "redirect:/admin/users";
        }

        // DEFENSE 2: Chặn xóa Admin cuối cùng
        if (userToDelete.getRole().getCode().equals("ADMIN")) {
            long adminCount = userRepository.countByRole_Code("ADMIN");
            if (adminCount <= 1) {
                ra.addFlashAttribute("errorMessage", "NGUY HIỂM: Không thể xóa Admin cuối cùng của hệ thống!");
                return "redirect:/admin/users";
            }
        }

        try {
            userRepository.deleteById(id);
            ra.addFlashAttribute("successMessage", "Đã xóa người dùng: " + userToDelete.getUsername());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi hệ thống: Không thể xóa user này.");
        }

        return "redirect:/admin/users";
    }

    // 7. SYSTEM LOGS
    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String viewLogs(Model model) {
        model.addAttribute("logs", auditLogService.getRecentLogs());
        return "admin/logs";
    }
}