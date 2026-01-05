package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private com.hcmute.qaute.service.DepartmentService departmentService; // Inject DepartmentService

    // Trang chủ công khai (Forum)
    @GetMapping("/")
    public String homePage(
            @org.springframework.web.bind.annotation.RequestParam(name = "dept", required = false) Integer deptId,
            @org.springframework.web.bind.annotation.RequestParam(name = "sort", required = false) String sort,
            @org.springframework.web.bind.annotation.RequestParam(name = "tag", required = false) String tag,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {

        // 1. Lấy danh sách câu hỏi đã filter
        List<QuestionResponseDTO> questions = questionService.getFilterQuestions(deptId, sort, tag);
        model.addAttribute("questions", questions);

        // 2. Lấy danh sách Department cho Sidebar
        model.addAttribute("departments", departmentService.getAllDepartments());

        // 3. Truyền lại params để UI highlight
        model.addAttribute("currentDeptId", deptId);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentTag", tag);

        // Trả về Fragment nếu là AJAX Request
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "home :: home_content";
        }

        return "home";
    }

    // Xem chi tiết (Ai cũng xem được, nhưng nút Reply sẽ bắt login)
    // Lưu ý: Logic lấy chi tiết bạn có thể tái sử dụng từ service

    // Tìm kiếm thông minh

    @GetMapping("/search")
    public String search(@org.springframework.web.bind.annotation.RequestParam("keyword") String keyword, Model model) {
        // Gọi Service tìm kiếm
        List<QuestionResponseDTO> results = questionService.searchQuestions(keyword);

        // Đẩy dữ liệu ra view
        model.addAttribute("questions", results);
        model.addAttribute("keyword", keyword);

        return "student/search_results"; // Trả về trang kết quả (sẽ tạo ở bước sau)
    }
}