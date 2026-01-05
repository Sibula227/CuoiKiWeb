package com.hcmute.qaute.controller;

import com.hcmute.qaute.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> payload, java.security.Principal principal) {
        String message = payload.get("message");
        String username = (principal != null) ? principal.getName() : "anonymous";

        // If anonymous, we might want to prevent chat or use a dummy session
        // For now, let's assume login is required or handle anonymous in service if
        // needed
        // But better to enforce login.

        String response = geminiService.getChatResponse(message, username);

        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        return result;
    }

    @PostMapping("/new")
    public Map<String, String> newChat(java.security.Principal principal) {
        String username = (principal != null) ? principal.getName() : "anonymous";
        geminiService.createSession(username);

        Map<String, String> result = new HashMap<>();
        result.put("message", "New session created");
        return result;
    }
}
