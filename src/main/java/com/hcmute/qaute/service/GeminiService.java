package com.hcmute.qaute.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final KnowledgeService knowledgeService;

    public GeminiService(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    public String getChatResponse(String userMessage) {
        try {
            Client client = new Client.Builder()
                    .apiKey(apiKey)
                    .build();

            String systemInstruction = "Bạn là một trợ lý ảo của trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE). " +
                    "Bạn trả lời các câu hỏi dựa trên thông tin sau:\n" +
                    knowledgeService.getKnowledgeBase() +
                    "\n\nNếu không tìm thấy thông tin trong dữ liệu này, hãy nói rằng bạn không biết hoặc đề nghị liên hệ phòng đào tạo. "
                    +
                    "Luôn trả lời bằng tiếng Việt lịch sự và thân thiện.";

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    systemInstruction + "\n\nUser Question: " + userMessage,
                    null);
            return response.text();

        } catch (Exception e) {
            log.error("Gemini Service Error", e);
            return "Xin lỗi, hiện tại tôi không thể trả lời (Lỗi hệ thống: " + e.getMessage() + ")";
        }
    }
}
