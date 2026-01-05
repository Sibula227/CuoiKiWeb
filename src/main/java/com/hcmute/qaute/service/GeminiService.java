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
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.LocalDateTime;

import com.hcmute.qaute.entity.ChatSession;
import com.hcmute.qaute.entity.ChatMessage;
import com.hcmute.qaute.repository.ChatSessionRepository;
import com.hcmute.qaute.repository.ChatMessageRepository;
import com.hcmute.qaute.repository.UserRepository;

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
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private Client client;

    public GeminiService(KnowledgeService knowledgeService,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository) {
        this.knowledgeService = knowledgeService;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
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

    public String getChatResponse(String userMessage, String username) {
        log.info("Processing chat request for user: '{}'", username);

        if (this.client == null) {
            return "Xin lỗi, dịch vụ AI chưa được khởi tạo đúng cách.";
        }

        try {
            // 1. Locate User and Session
            com.hcmute.qaute.entity.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Find latest active session or create new one
            ChatSession session = chatSessionRepository.findTopByUserOrderByUpdatedAtDesc(user)
                    .orElseGet(() -> {
                        ChatSession newSession = new ChatSession();
                        newSession.setUser(user);
                        newSession.setTitle("New Chat");
                        return chatSessionRepository.save(newSession);
                    });

            // 2. Load History (Last 20 messages)
            List<ChatMessage> history = chatMessageRepository.findBySessionOrderByCreatedAtDesc(
                    session,
                    org.springframework.data.domain.PageRequest.of(0, 20)).stream()
                    .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt())) // Ensure chronological order
                    .collect(java.util.stream.Collectors.toList());

            // 3. Prepare Prompt Resources
            String persona = loadResource(personaResource);
            String task = loadResource(taskResource);
            String contextTemplate = loadResource(contextResource);
            String format = loadResource(formatResource);

            String context = contextTemplate.replace(
                    "{KNOWLEDGE_BASE}",
                    knowledgeService.getKnowledgeBase());

            String systemInstructionText = String.join("\n\n", persona, task, context, format);

            // 4. Construct Gemini Request
            List<Content> contents = new ArrayList<>();

            // 4.1. Add History to Content
            for (ChatMessage msg : history) {
                contents.add(Content.builder()
                        .role(msg.getRole())
                        .parts(Collections.singletonList(Part.builder().text(msg.getContent()).build()))
                        .build());
            }

            // 4.2. Add Current Message
            contents.add(Content.builder()
                    .role("user")
                    .parts(Collections.singletonList(Part.builder().text(userMessage).build()))
                    .build());

            // 4.3. System Instruction
            Content systemInstructionContent = Content.builder()
                    .role("system")
                    .parts(Collections.singletonList(Part.builder().text(systemInstructionText).build()))
                    .build();

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(systemInstructionContent)
                    .temperature(0.7f)
                    .build();

            // 5. Call API
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    contents,
                    config);

            // 6. Extract Response
            String aiResponseText = "";
            if (response.text() != null) {
                aiResponseText = response.text();
            } else if (response.candidates().isPresent() && !response.candidates().get().isEmpty()) {
                // Fix: content() returns Optional<Content>, so use .get()
                aiResponseText = response.text();
            } else {
                return "Xin lỗi, AI không trả về phản hồi.";
            }

            // 7. Save to Database
            // User Message
            ChatMessage userMsgEntity = new ChatMessage();
            userMsgEntity.setSession(session);
            userMsgEntity.setRole("user");
            userMsgEntity.setContent(userMessage);
            chatMessageRepository.save(userMsgEntity);

            // AI Message
            ChatMessage modelMsgEntity = new ChatMessage();
            modelMsgEntity.setSession(session);
            modelMsgEntity.setRole("model");
            modelMsgEntity.setContent(aiResponseText);
            chatMessageRepository.save(modelMsgEntity);

            // Update Session
            session.setUpdatedAt(LocalDateTime.now());
            if (history.isEmpty()) {
                String potentialTitle = userMessage.length() > 50 ? userMessage.substring(0, 47) + "..." : userMessage;
                session.setTitle(potentialTitle);
            }
            chatSessionRepository.save(session);

            return aiResponseText;

        } catch (Exception e) {
            log.error("Gemini Service Error: ", e);
            return "Xin lỗi, hệ thống AI đang gặp lỗi. (" + e.getMessage() + ")";
        }
    }
}