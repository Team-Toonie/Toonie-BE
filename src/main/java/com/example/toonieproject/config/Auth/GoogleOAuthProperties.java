package com.example.toonieproject.config.Auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "google")
@Getter
@Setter
public class GoogleOAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
