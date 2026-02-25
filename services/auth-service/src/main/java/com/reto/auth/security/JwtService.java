package com.reto.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-minutes}")
    private int expirationMinutes;

    public String generateToken(String email, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        long expirationTime = System.currentTimeMillis() + (expirationMinutes * 60 * 1000);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(expirationTime))
                .signWith(key)
                .compact();
    }
}
