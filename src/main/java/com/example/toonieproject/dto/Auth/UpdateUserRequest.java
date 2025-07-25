package com.example.toonieproject.dto.Auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String phoneNumber;
    private String nickname;
}

