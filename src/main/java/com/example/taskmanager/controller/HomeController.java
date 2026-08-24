package com.example.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "task-manager");
        body.put("status", "up");
        body.put("timestamp", Instant.now());
        body.put("endpoints", Map.of(
                "register", "POST /api/auth/register",
                "login", "POST /api/auth/login",
                "projects", "GET/POST /api/projects",
                "tasks", "GET/POST /api/projects/{projectId}/tasks"
        ));
        return body;
    }
}

