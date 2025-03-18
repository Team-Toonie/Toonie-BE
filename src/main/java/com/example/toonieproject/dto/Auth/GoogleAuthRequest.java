package com.example.toonieproject.dto.Auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleAuthRequest {
    private String code;
    private String codeVerifier;
}
