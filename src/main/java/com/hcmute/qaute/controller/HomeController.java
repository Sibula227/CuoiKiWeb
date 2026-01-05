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

    // Trang chủ công khai (Forum)
    @GetMapping("/")
    public String homePage(Model model) {
        // Lấy tất cả câu hỏi (hoặc chỉ lấy câu đã trả lời tùy bạn)
        List<QuestionResponseDTO> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "home"; // Trả về file templates/home.html
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