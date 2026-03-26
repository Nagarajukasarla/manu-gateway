package com.api.gateway.service;

import com.api.gateway.utils.KeyLoader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final KeyLoader keyLoader;

    public Map<String, String> extractClaims(String token) {

        // Verify the token extract and claims
        Claims claims = Jwts.parser()
                .verifyWith(keyLoader.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Map.of(
                "username", String.valueOf(claims.getSubject()),
                "role", String.valueOf(claims.get("role"))
        );
    }
}
