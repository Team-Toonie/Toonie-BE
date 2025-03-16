package com.example.toonieproject.controller.Auth;


import com.example.toonieproject.dto.Auth.GoogleAuthRequest;
import com.example.toonieproject.dto.Auth.JwtToken;
import com.example.toonieproject.dto.Auth.RegisterRequest;
import com.example.toonieproject.service.Auth.AuthService;
import com.example.toonieproject.service.Auth.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth") // 로그인 전 호출 (인증X)
public class AuthController {

    private final GoogleOAuthService googleOAuthService;
    private final AuthService authService;

    @GetMapping("/login/google")
    public ResponseEntity<Map<String, String>> getGoogleLoginUrl(@RequestParam("codeChallenge") String codeChallenge) {

        String authUrl = googleOAuthService.buildAuthorizationUrl(codeChallenge);
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }

    @PostMapping("/callback/google")
    public ResponseEntity<?> googleAuthCallback(@RequestParam GoogleAuthRequest request) {

        JwtToken jwtToken = authService.loginWithGoogle(request.getCode(), request.getCodeVerifier());
        return ResponseEntity.ok(jwtToken);

    }

    @PostMapping("/register")
    public ResponseEntity<JwtToken> registerUser(@RequestBody RegisterRequest request,
                                                 @RequestHeader("Authorization") String token) {
        JwtToken jwtToken = authService.registerUser(request, token);
        return ResponseEntity.ok(jwtToken);
    }





}
