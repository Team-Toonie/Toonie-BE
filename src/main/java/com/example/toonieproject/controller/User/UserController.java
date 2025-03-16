package com.example.toonieproject.controller.User;

import com.example.toonieproject.config.Jwt.JwtTokenProvider;
import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(accessToken);
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }


    /*
    @GetMapping("/me")
    public ResponseEntity<Long> getCurrentUserId(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userId);
    }
    */



}
