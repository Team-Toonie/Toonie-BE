package com.example.toonieproject.dto.Auth;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CallbackResponse {
    private boolean isRegistered;

    // 기존 회원인 경우
    private String accessToken;
    private String refreshToken;

    // 신규 회원인 경우 프론트에 전달
    private String email;
    private String name;
}
