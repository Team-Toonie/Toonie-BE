package com.example.toonieproject.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshAccessTokenResponse {

    private String accessToken;
}
