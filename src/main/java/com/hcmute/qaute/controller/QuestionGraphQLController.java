package com.hcmute.qaute.controller;

import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class QuestionGraphQLController {

    @Autowired
    private QuestionService questionService;

    // Mapping với "searchQuestions" trong file schema.graphqls
    @QueryMapping
    public List<QuestionResponseDTO> searchQuestions(@Argument String keyword) {
        List<QuestionResponseDTO> allQuestions = questionService.getAllQuestions();
        
        // Nếu không có keyword, trả về rỗng hoặc list gốc tùy logic
        if (keyword == null || keyword.isEmpty()) {
            return allQuestions;
        }

        // Filter đơn giản bằng Java Stream (Thực tế nên query ở DB)
        String lowerKeyword = keyword.toLowerCase();
        return allQuestions.stream()
                .filter(q -> q.getTitle().toLowerCase().contains(lowerKeyword) 
                          || q.getContent().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
}