package com.example.toonieproject.service.Storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "firebase")
@Getter
@Setter
public class FirebaseProperties {
    private String bucketName;
    private String credentials;
}
