package com.example.minglethedog.security;

import com.example.minglethedog.dto.CustomUserDetails;
import com.example.minglethedog.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secret
            , @Value("${jwt.access-token-validity}") long accessTokenValidity
            , @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secret);
            this.secretKey = Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT secret key", e);
            throw new IllegalStateException("JWT secret key is invalid", e);
        }
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(CustomUserDetails customUserDetails) {
        String username = customUserDetails.getUsername();
        Role role = customUserDetails.getAuthorities().stream()
                .findFirst()
                .map(authority -> Role.valueOf(authority.getAuthority().replace("ROLE_", "")))
                .orElse(Role.USER); // 기본값 USER

        return createAccessToken(username, role, accessTokenValidity);
    }

    public String createRefreshToken(CustomUserDetails customUserDetails) {
        String username = customUserDetails.getUsername();
        return createRefreshToken(username, refreshTokenValidity);
    }

    private String createAccessToken(String username, Role role, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("role", role.name()) // 역할 정보 포함 (Access Token에만 포함)
                .claim("type", "access") // 명확한 타입 구분
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    private String createRefreshToken(String username, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh") // Refresh Token은 최소한의 정보만 포함
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public void validateToken(String token) {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }

    public String getUserRole(String token) {
        return getClaims(token).get("role", String.class); // "role" 클레임 값 가져오기

    }

    // JWT에서 클레임 추출 (중복 코드 최소화)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }
}


