package com.example.toonieproject.service.Auth;


import com.example.toonieproject.config.Auth.GoogleOAuthProperties;
import com.example.toonieproject.config.Jwt.JwtTokenProvider;
import com.example.toonieproject.dto.Auth.GoogleTokenResponse;
import com.example.toonieproject.dto.Auth.JwtToken;
import com.example.toonieproject.dto.Auth.RegisterRequest;
import com.example.toonieproject.entity.Auth.RefreshToken;
import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.repository.Auth.RefreshTokenRepository;
import com.example.toonieproject.repository.Auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    private final RestTemplate restTemplate;



    public JwtToken loginWithGoogle(String code, String codeVerifier) {

        // 1. 인가 코드 -> 구글 토큰 요청
        GoogleTokenResponse tokenResponse = getTokenFromGoogle(URLDecoder.decode(code, StandardCharsets.UTF_8), codeVerifier);
        String googleAccessToken = tokenResponse.getAccessToken();
        String googleRefreshToken = tokenResponse.getRefreshToken();

        System.out.println("googleAccessToken" + googleAccessToken);

        System.out.println("googleRefreshToken" + googleRefreshToken);


        // 2. 구글 액세스 토큰 -> 사용자 정보 조회
        Map<String, Object> userInfo = getGoogleUserInfo(googleAccessToken);
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        System.out.println(User.Role.GUEST);


        // 3. 사용자 정보 저장 or 조회
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole(User.Role.GUEST);
                    return userRepository.save(newUser);
                });


        // 4. 리프레시 토큰 저장
        refreshTokenRepository.save(new RefreshToken(user.getId(), googleRefreshToken));


        // 5. JWT 액세스/리프레시 토큰 생성
        String jwtAccessToken = jwtTokenProvider.generateToken(user, Duration.ofMinutes(30));
        String jwtRefreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));



        return new JwtToken(jwtAccessToken, jwtRefreshToken);
    }

    private GoogleTokenResponse getTokenFromGoogle(String code, String codeVerifier) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // test
        System.out.println("==== Google Token Request ====");
        System.out.println("client_id: " + googleOAuthProperties.getClientId());
        System.out.println("client_secret: " + googleOAuthProperties.getClientSecret());
        System.out.println("code: " + code);
        System.out.println("code_verifier: " + codeVerifier);
        System.out.println("redirect_uri: " + googleOAuthProperties.getRedirectUri());
        System.out.println("================================");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", googleOAuthProperties.getClientId());
        body.add("client_secret", googleOAuthProperties.getClientSecret());
        body.add("code", code);
        body.add("code_verifier", codeVerifier);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", googleOAuthProperties.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                GoogleTokenResponse.class
        );

        return response.getBody();
    }

    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        //RestTemplate restTemplate = new RestTemplate();

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
