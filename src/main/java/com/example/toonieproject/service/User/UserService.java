package com.example.toonieproject.service.User;

import com.example.toonieproject.dto.Auth.UpdateUserRequest;
import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.repository.Auth.RefreshTokenRepository;
import com.example.toonieproject.repository.Auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public User findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }


    // 탈퇴
    @Transactional
    public void deleteUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. refreshToken 먼저 삭제
        refreshTokenRepository.deleteByUserId(userId);

        userRepository.delete(user); // 실제 삭제
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        // save()는 생략 가능 — JPA의 변경 감지(Dirty Checking)로 자동 반영됨
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }


}
