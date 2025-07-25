package com.example.toonieproject.dto.Auth;

import com.example.toonieproject.entity.Auth.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDetailUserRequest {
    private String phoneNumber;
    private User.Role role; // OWNER 또는 CUSTOMER
    private String nickname;
}
