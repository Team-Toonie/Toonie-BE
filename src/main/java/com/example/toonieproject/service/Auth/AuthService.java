package com.example.toonieproject.service.Auth;


import com.example.toonieproject.config.Auth.GoogleOAuthProperties;
import com.example.toonieproject.config.Jwt.JwtTokenProvider;
import com.example.toonieproject.dto.Auth.JwtToken;
import com.example.toonieproject.dto.Auth.RegisterRequest;
import com.example.toonieproject.entity.Auth.RefreshToken;
import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.repository.Auth.RefreshTokenRepository;
import com.example.toonieproject.repository.Auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleOAuthProperties googleOAuthProperties;


    public JwtToken loginWithGoogle(String code, String codeVerifier) {

        // 1. 인가 코드 -> 구글 토큰 요청
        Map<String, String> tokenResponse = getTokenFromGoogle(code, codeVerifier);

        String googleAccessToken = tokenResponse.get("access_token");
        String googleRefreshToken = tokenResponse.get("refresh_token");


        // 2. 구글 액세스 토큰 -> 사용자 정보 조회
        Map<String, Object> userInfo = getGoogleUserInfo(googleAccessToken);
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");


        // 3. 사용자 정보 저장 or 조회
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    //newUser.setRole(null);
                    return userRepository.save(newUser);
                });


        // 4. 리프레시 토큰 저장
        refreshTokenRepository.save(new RefreshToken(user.getId(), googleRefreshToken));


        // 5. JWT 액세스/리프레시 토큰 생성
        String jwtAccessToken = jwtTokenProvider.generateToken(user, Duration.ofMinutes(30));
        String jwtRefreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));

        return new JwtToken(jwtAccessToken, jwtRefreshToken);
    }

    private Map<String, String> getTokenFromGoogle(String code, String codeVerifier) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("client_id", googleOAuthProperties.getClientId());
        params.put("client_secret", googleOAuthProperties.getClientSecret());
        params.put("code", code);
        params.put("code_verifier", codeVerifier);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", googleOAuthProperties.getRedirectUri());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                Map.class
        );

        return response.getBody();
    }

    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                request,
                Map.class
        );

        return response.getBody();
    }


    public JwtToken registerUser(RegisterRequest request, String bearerToken) {
        String accessToken = bearerToken.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 추가 정보 등록
        user.setRole(request.getRole());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user); // 기존 유저 업데이트

        // 새 accessToken 발급 (role 정보 반영)
        String newAccessToken = jwtTokenProvider.generateToken(user, Duration.ofMinutes(30));
        String newRefreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));

        refreshTokenRepository.save(new RefreshToken(user.getId(), newRefreshToken));

        return new JwtToken(newAccessToken, newRefreshToken);
    }


}
