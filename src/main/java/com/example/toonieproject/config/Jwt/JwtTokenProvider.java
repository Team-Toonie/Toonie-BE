package com.example.toonieproject.config.Jwt;


import com.example.toonieproject.entity.Auth.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    // JWT 토큰 생성
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredAt.toMillis());

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId())) // 사용자 ID를 subject로
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();

    }


    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            return false;
        } catch (JwtException e) {
            // 그 외 에러 (위조 등)
            return false;
        }
    }

    // JWT에서 사용자 ID추출
    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

}
