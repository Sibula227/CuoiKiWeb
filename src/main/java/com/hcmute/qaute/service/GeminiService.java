package com.hcmute.qaute.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;

@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("classpath:prompts/persona.txt")
    private Resource personaResource;

    @Value("classpath:prompts/task.txt")
    private Resource taskResource;

    @Value("classpath:prompts/context.txt")
    private Resource contextResource;

    @Value("classpath:prompts/format.txt")
    private Resource formatResource;

    private final KnowledgeService knowledgeService;
    private Client client;

    public GeminiService(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostConstruct
    public void initClient() {
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            log.error("API Key for Gemini is missing!");
        }
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    private String loadResource(Resource resource) {
        try {
            return StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error loading prompt resource: {}", resource.getFilename(), e);
            return "";
        }
    }

    public String getChatResponse(String userMessage) {
        try {
            String persona = loadResource(personaResource);
            String task = loadResource(taskResource);
            String contextTemplate = loadResource(contextResource);
            String format = loadResource(formatResource);

            String context = contextTemplate.replace(
                    "{KNOWLEDGE_BASE}",
                    knowledgeService.getKnowledgeBase());

            String systemInstructionText = String.join("\n\n", persona, task, context, format);

            // === FIX BẮT ĐẦU TỪ ĐÂY ===

            // 1. Tạo Part chứa text cho System Instruction
            Part systemPart = Part.builder()
                    .text(systemInstructionText)
                    .build();

            // 2. Tạo Content chứa Part đó
            Content systemInstructionContent = Content.builder()
                    // .role("system") // Lưu ý: Một số version SDK không cần role cho system
                    // instruction, nếu lỗi role thì bỏ dòng này
                    .parts(Collections.singletonList(systemPart))
                    .build();

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(systemInstructionContent)
                    .temperature(0.7f)
                    .build();

            // 3. Tạo Part cho User Message
            Part userPart = Part.builder()
                    .text(userMessage)
                    .build();

            // 4. Tạo Content cho User Message
            Content userContent = Content.builder()
                    .role("user")
                    .parts(Collections.singletonList(userPart))
                    .build();

            // === FIX KẾT THÚC ===

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    userContent,
                    config);

            // Lưu ý: Nếu response.text() vẫn báo lỗi (tuỳ version SDK), hãy dùng dòng dưới:
            // return response.candidates().get(0).content().parts().get(0).text();
            return response.text();

        } catch (Exception e) {
            log.error("Gemini Service Error: ", e);
            return "Xin lỗi, hệ thống AI đang gặp lỗi kết nối.";
        }
    }
}