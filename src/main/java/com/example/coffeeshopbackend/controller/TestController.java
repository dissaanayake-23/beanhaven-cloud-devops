package com.example.coffeeshopbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class TestController {

    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend is running!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("pong", String.valueOf(System.currentTimeMillis()));
        return response;
    }

    @GetMapping("/admin/test-auth")
    public Map<String, Object> testAuth(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());

        if (authHeader == null) {
            response.put("authenticated", false);
            response.put("message", "No Authorization header");
        } else if (!authHeader.startsWith("Bearer ")) {
            response.put("authenticated", false);
            response.put("message", "Invalid Authorization format");
            response.put("header", authHeader);
        } else {
            String token = authHeader.substring(7);
            response.put("authenticated", true);
            response.put("tokenLength", token.length());
            response.put("message", "Token accepted for demo");
        }

        return response;
    }
}