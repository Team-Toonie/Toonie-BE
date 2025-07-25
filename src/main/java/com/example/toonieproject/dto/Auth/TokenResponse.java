package com.example.toonieproject.dto.Auth;

import com.example.toonieproject.entity.Auth.User;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;

    private User.Role role; // OWNER or CUSTOMER or TEMPUSER
}

