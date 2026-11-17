package com.example.jwt;

import static org.junit.jupiter.api.Assertions.*;
import com.example.jwt.config.JwtProperties;
import com.example.jwt.service.JwtService;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
    @Test
    void generatedTokenCanBeValidatedAndContainsPayload() {
        String secret = Base64.getEncoder().encodeToString(
                "01234567890123456789012345678901".getBytes());
        var key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        var service = new JwtService(key, new JwtProperties(secret, 30));
        String token = service.generateToken(Map.of("sub", "venu", "role", "USER"));
        var claims = service.validateAndRead(token);
        assertEquals("venu", claims.getSubject());
        assertEquals("USER", claims.get("role"));
    }
}
