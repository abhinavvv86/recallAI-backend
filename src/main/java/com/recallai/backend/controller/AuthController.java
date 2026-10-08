package com.recallai.backend.controller;

import com.recallai.backend.dto.AuthResponse;
import com.recallai.backend.dto.LoginRequest;
import com.recallai.backend.dto.RegisterRequest;
import com.recallai.backend.model.User;
import com.recallai.backend.security.JwtService;
import com.recallai.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword());

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse(
                            null,
                            "Invalid email or password"));
        }

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        "Login successful"));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("JWT authentication is working!");
    }
}