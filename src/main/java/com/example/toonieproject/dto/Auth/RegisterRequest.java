package com.example.toonieproject.dto.Auth;

import com.example.toonieproject.entity.Auth.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private User.Role role;         // OWNER or CUSTOMER
    private String phoneNumber;     // 전화번호
}
