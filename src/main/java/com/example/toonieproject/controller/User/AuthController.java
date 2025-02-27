package com.example.toonieproject.controller.User;


import com.example.toonieproject.service.User.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
//    @GetMapping("/login/google")
//    public ResponseEntity<?> getGoogleLoginUrl() {
//        String authUrl = authService.getGoogleLoginUrl();
//        return ResponseEntity.ok(Map.of("authUrl", authUrl));
//    }
//
//    @GetMapping("/callback/google")
//    public ResponseEntity<?> googleAuthCallback(@RequestParam String code) {
//        JwtToken jwtToken = authService.authenticateWithGoogle(code);
//        return ResponseEntity.ok(jwtToken);
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
//        JwtToken jwtToken = authService.registerUser(request);
//        return ResponseEntity.ok(jwtToken);
//    }
//
//    @PostMapping("/token/refresh")
//    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
//        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
//        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
//        authService.logout(token);
//        return ResponseEntity.ok(Map.of("message", "Logout successful"));
//    }



}
