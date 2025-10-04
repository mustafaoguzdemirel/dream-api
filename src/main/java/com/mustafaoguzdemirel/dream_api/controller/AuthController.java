package com.mustafaoguzdemirel.dream_api.controller;

import com.mustafaoguzdemirel.dream_api.dto.ApiResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/anonymous")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createAnonymousUser() {
        AppUser user = authService.createAnonymousUser();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());

        return ResponseEntity.ok(ApiResponse.success("Anonymous user created successfully", data));
    }
}
