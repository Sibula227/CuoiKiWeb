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
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response = geminiService.getChatResponse(message);

        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        return result;
    }
}
