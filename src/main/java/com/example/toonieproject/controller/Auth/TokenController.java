package com.example.toonieproject.controller.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/token")
public class TokenController {

//    @PostMapping("/refresh")
//    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
//        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
//        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
//    }




}
