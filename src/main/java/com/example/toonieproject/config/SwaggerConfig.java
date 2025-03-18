package com.example.toonieproject.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // SecurityScheme 등록
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // 또는 .APIKEY도 가능하지만 보통 HTTP 방식으로
                .scheme("bearer")
                .bearerFormat("JWT") // (optional) 보여주기용
                .in(SecurityScheme.In.HEADER)
                .name("Authorization"); // 헤더 이름

        // Security Requirement 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization"); // 위에서 설정한 name과 동일해야 함

        return new OpenAPI()
                .info(new Info()
                        .title("Toonie API")
                        .version("v1.0")
                        .description("만화카페 만화 대여 프로젝트 API 문서"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", securityScheme)) // 키 이름 통일
                .addSecurityItem(securityRequirement);
    }
}
