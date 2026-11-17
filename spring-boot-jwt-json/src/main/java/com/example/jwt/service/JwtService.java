package com.example.jwt.service;

import com.example.jwt.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final JwtProperties properties;

    public JwtService(SecretKey signingKey, JwtProperties properties) {
        this.signingKey = signingKey;
        this.properties = properties;
    }

    public String generateToken(Map<String, Object> payload) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(properties.expirationMinutes() * 60);
        return Jwts.builder()
                .claims(payload)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public Claims validateAndRead(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
