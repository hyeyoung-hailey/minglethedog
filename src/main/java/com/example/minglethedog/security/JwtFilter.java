package com.example.minglethedog.security;

import com.example.minglethedog.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * OnceOerRequestFilter를 구현하면, 모든요청에서 자동으로 JWT를 검증하고
 * 인증정보를 SecurityContext에 설정할 수 있다.
 * 컨트롤러에서 직접 JWT 검증을 수행하지 않아도 된다.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 토큰이 유효하다면 SecurityContext에 인증 정보 설정
        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token);
                setAuthentication(token);
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, "TOKEN_EXPIRED", "Access Token has expired");
                return;
            } catch (JwtException e) {
                sendErrorResponse(response, "INVALID_TOKEN", "Invalid JWT Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 Authorization 헤더 추출 및 Bearer 토큰 파싱
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            return token.isEmpty() ? null : token; // 빈 문자열 방지
        }
        return null;
    }

    // 토큰을 통해 인증 객체를 생성하고 SecurityContext에 설정
    private void setAuthentication(String token) {
        String username = jwtTokenProvider.getUsername(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendErrorResponse(HttpServletResponse response, String errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("message", message);

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
