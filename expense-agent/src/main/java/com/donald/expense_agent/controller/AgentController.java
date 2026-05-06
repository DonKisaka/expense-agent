package com.donald.expense_agent.controller;

import com.donald.expense_agent.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestHeader(value = "X-Session-Id", defaultValue = "default-session") String sessionId,
            @RequestBody String message) {

        String response = agentService.chat(sessionId, message);
        return ResponseEntity.ok(response);
    }
}
