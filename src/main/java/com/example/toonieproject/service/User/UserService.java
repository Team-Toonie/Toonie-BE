package com.example.toonieproject.service.User;

import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.repository.Auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }


    // 탈퇴

}
