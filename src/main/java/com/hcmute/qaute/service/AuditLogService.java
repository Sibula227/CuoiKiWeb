package com.hcmute.qaute.service;

import com.hcmute.qaute.entity.AuditLog;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.repository.AuditLogRepository;
import com.hcmute.qaute.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Transactional
    public void log(String username, String action, String objectType, String objectId, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setObjectType(objectType);
        log.setObjectId(objectId);

        // Ensure details is valid JSON for MySQL JSON column
        if (details != null && !details.trim().startsWith("{") && !details.trim().startsWith("[")) {
            try {
                java.util.Map<String, String> map = new java.util.HashMap<>();
                map.put("active_info", details);
                details = objectMapper.writeValueAsString(map);
            } catch (Exception e) {
                details = "{\"error\": \"Could not serialize details\"}";
            }
        }

        log.setDetails(details);

        // Find User ID
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            log.setUserId(userOpt.get().getId());
        } else {
            // Log as system or anonymous with Id 0 or null
            // log.setUserId(null);
        }

        // IP Address handling could be done via RequestContextHolder if needed,
        // for now let's leave it null or pass it if available.
        log.setIpAddress("127.0.0.1"); // Placeholder or implement IP extraction util

        auditLogRepository.save(log);
    }

    public java.util.List<AuditLog> getRecentLogs() {
        // Return top 100 logs
        return auditLogRepository.findAll(org.springframework.data.domain.Sort
                .by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        // Real implementation should use PageRequest, effectively:
        // return auditLogRepository.findAll(PageRequest.of(0, 100,
        // Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }
}
