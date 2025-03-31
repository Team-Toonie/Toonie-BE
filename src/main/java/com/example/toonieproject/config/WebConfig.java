package com.example.toonieproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8443", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 최소한의 HTTP 메서드 허용
                .allowedHeaders("*")
                .allowCredentials(true) // 쿠키 인증 허용
                .maxAge(3600) // Preflight 요청을 1시간 동안 캐싱
                .allowedMethods(HttpMethod.GET.name());
    }
}
