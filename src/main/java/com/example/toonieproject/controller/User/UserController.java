package com.example.toonieproject.controller.User;

import com.example.toonieproject.config.Jwt.JwtTokenProvider;
import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.service.User.UserService;
import com.example.toonieproject.util.auth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        User user = userService.findById(currentUserId);

        return ResponseEntity.ok(user);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<String> deleteCurrentUser() {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        userService.deleteUserById(currentUserId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }





}
