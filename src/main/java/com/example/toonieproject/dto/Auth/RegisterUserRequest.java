package com.example.toonieproject.dto.Auth;

import com.example.toonieproject.entity.Auth.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {
    private String email;
    private String name;
    private String phoneNumber;
    private User.Role role; // OWNER 또는 CUSTOMER
}
