package com.example.jwt.controller;

import com.example.jwt.service.JwtService;
import io.jsonwebtoken.Claims;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {
    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@RequestBody Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "JSON payload must not be empty"));
        }
        // Prevent caller-supplied registered claims from overriding server-controlled timestamps.
        if (payload.containsKey("iat") || payload.containsKey("exp") || payload.containsKey("nbf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Do not supply iat, exp, or nbf claims"));
        }
        String token = jwtService.generateToken(payload);
        return ResponseEntity.ok(Map.of("token", token, "tokenType", "Bearer"));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "token is required"));
        }
        try {
            Claims claims = jwtService.validateAndRead(token);
            return ResponseEntity.ok(Map.of("valid", true, "claims", claims));
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid or expired token"));
        }
    }
}
