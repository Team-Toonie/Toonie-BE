package com.example.toonieproject.util.auth;

import com.example.toonieproject.dto.Auth.TokenUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authentication found");
        }

        TokenUserDetails principal = (TokenUserDetails) authentication.getPrincipal();
        return principal.getEmail();
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authentication found");
        }

        TokenUserDetails principal = (TokenUserDetails) authentication.getPrincipal();
        return principal.getId();
    }
}