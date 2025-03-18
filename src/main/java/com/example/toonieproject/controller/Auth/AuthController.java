package com.example.toonieproject.controller.Auth;


import com.example.toonieproject.dto.Auth.GoogleAuthRequest;
import com.example.toonieproject.dto.Auth.JwtToken;
import com.example.toonieproject.dto.Auth.RefreshTokenRequest;
import com.example.toonieproject.dto.Auth.RegisterRequest;
import com.example.toonieproject.service.Auth.AuthService;
import com.example.toonieproject.service.Auth.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final GoogleOAuthService googleOAuthService;
    private final AuthService authService;

    @GetMapping("/login/google")
    public ResponseEntity<Map<String, String>> getGoogleLoginUrl(@RequestParam("codeChallenge") String codeChallenge) {

        String authUrl = googleOAuthService.buildAuthorizationUrl(codeChallenge);
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }

    @PostMapping("/callback/google")
    public ResponseEntity<?> googleAuthCallback(@RequestBody GoogleAuthRequest request) {

        JwtToken jwtToken = authService.loginWithGoogle(request.getCode(), request.getCodeVerifier());
        return ResponseEntity.ok(jwtToken);

    }

    @PostMapping("/register") // 전화번호 인증 후 전화번호 저장
    public ResponseEntity<JwtToken> registerUser(@RequestBody RegisterRequest request,
                                                 @RequestHeader("Authorization") String bearerToken) {
        JwtToken jwtToken = authService.registerUser(request, bearerToken);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("token/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody RefreshTokenRequest request) {

        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String bearerToken) {
        authService.logout(bearerToken);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }






}
