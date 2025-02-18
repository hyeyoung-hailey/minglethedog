package com.example.minglethedog.controller;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.EntityType;
import com.example.minglethedog.common.ErrorCode;
import com.example.minglethedog.dto.*;
import com.example.minglethedog.entity.Role;
import com.example.minglethedog.entity.User;
import com.example.minglethedog.exception.BusinessException;
import com.example.minglethedog.repository.UserRepository;
import com.example.minglethedog.security.JwtTokenProvider;
import com.example.minglethedog.service.RedisService;
import com.example.minglethedog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        User user = userService.createUser(signupRequest.getUsername(), signupRequest.getPassword(), Role.ADMIN);
        SignupResponse signupResponse = new SignupResponse(user.getId());
        return ResponseEntity.ok(ApiResponse.resource(EntityType.USER, signupResponse));

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequest loginRequest) {
        Authentication athentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        //인증된 사용자 정보 SecurityContextHolder에 저장
        SecurityContextHolder.getContext().setAuthentication(athentication);

        //JWT 발급
        CustomUserDetails userDetails = (CustomUserDetails) athentication.getPrincipal();
        String accessToken = jwtTokenProvider.createAccessToken(userDetails);  // JWT 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(userDetails); // Refresh Token 생성

        // Redis에 Refresh Token 저장 (7일 유지)
        redisService.saveRefreshToken(userDetails.getUsername(), refreshToken);

        JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken);
        return ResponseEntity.ok(ApiResponse.resource(EntityType.JWT, jwtResponse));
    }


    /**
     * Refresh Token을 이용한 Access Token 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(@AuthenticationPrincipal CustomUserDetails userDetails
            , @RequestBody RefreshRequest request) {
        String refreshToken = redisService.getRefreshToken(userDetails.getUsername());

        if (refreshToken == null || !refreshToken.equals(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userDetails);  // JWT 생성
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userDetails); // Refresh Token 생성

        redisService.deleteRefreshToken(userDetails.getUsername());
        redisService.saveRefreshToken(userDetails.getUsername(), newRefreshToken);

        JwtResponse jwtResponse = new JwtResponse(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(ApiResponse.resource(EntityType.JWT, jwtResponse));
    }


    /**
     * 로그아웃 (Redis에서 Refresh Token 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody RefreshRequest request) {
        String storedRefreshToken = redisService.getRefreshToken(userDetails.getUsername());
        if (storedRefreshToken == null || !storedRefreshToken.equals(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        redisService.deleteRefreshToken(userDetails.getUsername());
        return ResponseEntity.ok("Logged out successfully");
    }

}
