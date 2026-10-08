package com.recallai.backend.controller;

import com.recallai.backend.model.User;
import com.recallai.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private final UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("JWT protected endpoint is working!");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/auth-check")
    public ResponseEntity<String> authCheck(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                "Authenticated as: " + authentication.getName()
        );
    }
}