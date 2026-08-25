package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.Role;
import com.example.coffeeshopbackend.entity.User;
import com.example.coffeeshopbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> credentials) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, String> response = new HashMap<>();

        // =====================================================
        // VALIDATE INPUT
        // =====================================================

        if (username == null ||
                password == null ||
                username.isBlank() ||
                password.isBlank()) {

            response.put(
                    "message",
                    "Username and password are required"
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }


        // =====================================================
        // FIND USER
        // =====================================================

        Optional<User> optionalUser =
                userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {

            response.put(
                    "message",
                    "Invalid username or password"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        User user = optionalUser.get();


        // =====================================================
        // CHECK ADMIN ROLE
        // =====================================================

        if (user.getRole() != Role.ADMIN) {

            response.put(
                    "message",
                    "Access denied"
            );

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }


        // =====================================================
        // CHECK ACCOUNT STATUS
        // =====================================================

        if (!user.isEnabled()) {

            response.put(
                    "message",
                    "Admin account is disabled"
            );

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }


        // =====================================================
        // VERIFY PASSWORD
        // =====================================================

        if (!passwordEncoder.matches(
                password,
                user.getPassword()
        )) {

            response.put(
                    "message",
                    "Invalid username or password"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }


        // =====================================================
        // LOGIN SUCCESS
        // =====================================================

        response.put(
                "message",
                "Admin login successful"
        );

        response.put(
                "username",
                user.getUsername()
        );

        response.put(
                "role",
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}