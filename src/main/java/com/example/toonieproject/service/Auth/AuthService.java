package com.example.toonieproject.service.Auth;

import com.example.toonieproject.config.Auth.GoogleOAuthProperties;
import com.example.toonieproject.config.Jwt.JwtTokenProvider;
import com.example.toonieproject.dto.Auth.*;
import com.example.toonieproject.entity.Auth.RefreshToken;
import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.repository.Auth.RefreshTokenRepository;
import com.example.toonieproject.repository.Auth.UserRepository;
import com.example.toonieproject.util.auth.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleOAuthProperties googleOAuthProperties;
    private final RestTemplate restTemplate;



    public TokenResponse loginWithGoogle(String code, String codeVerifier) {

        // 1. 인가 코드 -> 구글 토큰 요청
        GoogleTokenResponse tokenResponse = getTokenFromGoogle(URLDecoder.decode(code, StandardCharsets.UTF_8), codeVerifier);
        String googleAccessToken = tokenResponse.getAccessToken();
        String googleRefreshToken = tokenResponse.getRefreshToken();


        // 2. 구글 액세스 토큰 -> 사용자 정보 조회
        Map<String, Object> userInfo = getGoogleUserInfo(googleAccessToken);
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");


        // 3. 이메일로 기존 회원 검색
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            // 4-1.기존 회원이면 → 리프레시 토큰 저장 → JWT 반환
            User user = optionalUser.get();

            // DB에 RefreshToken 저장/업데이트
            refreshTokenRepository.save(new RefreshToken(user.getId(), googleRefreshToken));

            String jwtAccessToken = jwtTokenProvider.generateToken(user, Duration.ofMinutes(30));
            String jwtRefreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));

            return new TokenResponse(jwtAccessToken, jwtRefreshToken, user.getRole());
        } else {
            // 4-2. 신규 회원이면 → 임시 회원 가입

            // 새로운 User 객체 생성 (최초 INSERT)
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole(User.Role.valueOf("TEMPUSER"));

            // 저장
            userRepository.save(user);

            // JWT 발급
            String accessToken = jwtTokenProvider.generateToken(user, Duration.ofMinutes(30));
            String refreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));

            // RefreshToken 저장
            refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));

            return new TokenResponse(accessToken, refreshToken, User.Role.valueOf("TEMPUSER"));
        }
    }


    public void registerUser(RegisterDetailUserRequest request) {

        // jwt에서 email추출
        String email = SecurityUtil.getCurrentUserEmail();
        System.out.println(email);

        // user 정보 설정
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setRole(request.getRole());
        user.setNickname(request.getNickname());
        user.setPhoneNumber(request.getPhoneNumber());

        // 저장
        userRepository.save(user);

    }


    public RefreshAccessTokenResponse refreshAccessToken(String refreshToken) {
        // 1. JWT 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. DB에 저장된 refreshToken과 비교
        RefreshToken savedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰이 없습니다."));

        // 3. 해당 userId로 사용자 조회
        User user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 4. 새로운 accessToken 발급
        return new RefreshAccessTokenResponse(jwtTokenProvider.generateToken(user, Duration.ofMinutes(30)));
    }


    @Transactional
    public void logout(String bearerToken) {
        String accessToken = bearerToken.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(accessToken);

        // 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserId(userId);
    }

    /*
        method
     */

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

}


