package com.example.toonieproject.controller.Auth;


import com.example.toonieproject.dto.Auth.*;
import com.example.toonieproject.service.Auth.AuthService;
import com.example.toonieproject.service.Auth.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final GoogleOAuthService googleOAuthService;
    private final AuthService authService;

    @GetMapping("/login/google") // 인가서버 요청 url
    public ResponseEntity<Map<String, String>> getGoogleLoginUrl(@RequestParam("codeChallenge") String codeChallenge) {

        String authUrl = googleOAuthService.buildAuthorizationUrl(codeChallenge);
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }

    @PostMapping("/callback/google") // 로그인
    public ResponseEntity<TokenResponse> googleAuthCallback(@RequestBody GoogleAuthRequest request) {

        TokenResponse tokenResponse = authService.loginWithGoogle(request.getCode(), request.getCodeVerifier());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register/detail-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TokenResponse> registerDetailUser(@RequestBody RegisterDetailUserRequest request) {

        TokenResponse tokenResponse = authService.registerUser(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("token/refresh")
    public ResponseEntity<RefreshAccessTokenResponse> refreshAccessToken(@RequestBody RefreshAccessTokenRequest request) {

        RefreshAccessTokenResponse refreshAccessTokenResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(refreshAccessTokenResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String bearerToken) {

        authService.logout(bearerToken);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }






}
