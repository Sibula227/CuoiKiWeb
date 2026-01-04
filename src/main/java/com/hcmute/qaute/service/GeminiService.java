package com.hcmute.qaute.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

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

    public GeminiService(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    private String loadResource(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error loading prompt resource: " + resource.getFilename(), e);
            return "";
        }
    }

    public String getChatResponse(String userMessage) {
        try {
            String persona = loadResource(personaResource);
            String task = loadResource(taskResource);
            String contextTemplate = loadResource(contextResource);
            String format = loadResource(formatResource);

            String context = contextTemplate.replace("{KNOWLEDGE_BASE}", knowledgeService.getKnowledgeBase());

            String systemInstruction = String.join("\n\n", persona, task, context, format);

            Client client = new Client.Builder()
                    .apiKey(apiKey)
                    .build();

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
