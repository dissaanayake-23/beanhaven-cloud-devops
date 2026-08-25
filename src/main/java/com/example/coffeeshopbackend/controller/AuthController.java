package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.Role;
import com.example.coffeeshopbackend.entity.User;
import com.example.coffeeshopbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @RequestBody Map<String, String> signupRequest) {

        Map<String, Object> response = new HashMap<>();

        try {

            String username = signupRequest.get("username");
            String email = signupRequest.get("email");
            String password = signupRequest.get("password");
            String fullName = signupRequest.get("fullName");

            if (username == null ||
                    email == null ||
                    password == null ||
                    username.isBlank() ||
                    email.isBlank() ||
                    password.isBlank()) {

                response.put("success", false);
                response.put(
                        "message",
                        "Username, email and password are required."
                );

                return ResponseEntity.badRequest().body(response);
            }

            if (userRepository.existsByUsername(username)) {

                response.put("success", false);
                response.put(
                        "message",
                        "Username already exists!"
                );

                return ResponseEntity.badRequest().body(response);
            }

            if (userRepository.existsByEmail(email)) {

                response.put("success", false);
                response.put(
                        "message",
                        "Email already exists!"
                );

                return ResponseEntity.badRequest().body(response);
            }

            User user = new User(
                    username.trim(),
                    email.trim(),
                    passwordEncoder.encode(password)
            );

            user.setFullName(fullName);

            // Users registering from the website
            // must NEVER be able to create an ADMIN.
            user.setRole(Role.USER);

            userRepository.save(user);

            response.put("success", true);
            response.put(
                    "message",
                    "User registered successfully!"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put(
                    "message",
                    "Registration failed."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody Map<String, String> loginRequest) {

        Map<String, Object> response = new HashMap<>();

        try {

            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            if (username == null ||
                    password == null ||
                    username.isBlank() ||
                    password.isBlank()) {

                response.put("success", false);
                response.put(
                        "message",
                        "Username and password are required."
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            Optional<User> userOptional =
                    userRepository.findByUsername(username.trim());

            /*
             * Keep the same message for unknown username
             * and wrong password.
             *
             * This avoids telling attackers whether
             * a username exists.
             */
            if (userOptional.isEmpty()) {

                response.put("success", false);
                response.put(
                        "message",
                        "Invalid username or password."
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            User user = userOptional.get();


            // Check whether account is enabled
            if (!user.isEnabled()) {

                response.put("success", false);
                response.put(
                        "message",
                        "This account is disabled."
                );

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(response);
            }


            // Check whether account is currently locked
            if (user.getLockTime() != null) {

                LocalDateTime unlockTime =
                        user.getLockTime()
                                .plusMinutes(LOCK_TIME_MINUTES);

                if (LocalDateTime.now().isBefore(unlockTime)) {

                    long secondsRemaining =
                            Duration.between(
                                    LocalDateTime.now(),
                                    unlockTime
                            ).getSeconds();

                    long minutesRemaining =
                            (secondsRemaining / 60) + 1;

                    response.put("success", false);
                    response.put("locked", true);
                    response.put(
                            "minutesRemaining",
                            minutesRemaining
                    );

                    response.put(
                            "message",
                            "Too many failed login attempts. "
                                    + "Account locked. Try again in "
                                    + minutesRemaining
                                    + " minute(s)."
                    );

                    return ResponseEntity
                            .status(HttpStatus.TOO_MANY_REQUESTS)
                            .body(response);
                }

                // Lock period finished
                user.setFailedAttempts(0);
                user.setLockTime(null);

                userRepository.save(user);
            }


            // Verify password
            if (!passwordEncoder.matches(
                    password,
                    user.getPassword())) {

                int newFailedAttempts =
                        user.getFailedAttempts() + 1;

                user.setFailedAttempts(newFailedAttempts);

                if (newFailedAttempts >= MAX_FAILED_ATTEMPTS) {

                    user.setLockTime(LocalDateTime.now());

                    userRepository.save(user);

                    response.put("success", false);
                    response.put("locked", true);

                    response.put(
                            "message",
                            "Too many failed attempts. "
                                    + "Account locked for 15 minutes."
                    );

                    return ResponseEntity
                            .status(HttpStatus.TOO_MANY_REQUESTS)
                            .body(response);
                }

                userRepository.save(user);

                int attemptsRemaining =
                        MAX_FAILED_ATTEMPTS
                                - newFailedAttempts;

                response.put("success", false);
                response.put(
                        "attemptsRemaining",
                        attemptsRemaining
                );

                response.put(
                        "message",
                        "Invalid username or password. "
                                + attemptsRemaining
                                + " attempt(s) remaining."
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }


            // Password correct -> reset failed attempts
            user.setFailedAttempts(0);
            user.setLockTime(null);

            userRepository.save(user);


            // Return user information
            response.put("success", true);
            response.put(
                    "message",
                    "Login successful!"
            );

            response.put(
                    "user",
                    Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "fullName",
                            user.getFullName() == null
                                    ? ""
                                    : user.getFullName(),
                            "role", user.getRole()
                    )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put(
                    "message",
                    "Login failed."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}