package com.example.toonieproject.service.Storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseStorageService {


    private final FirebaseProperties firebaseProperties;
    private Storage storage;
    @PostConstruct
    public void init() throws IOException {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(
                        new FileInputStream(firebaseProperties.getCredentials())
                ))
                .build()
                .getService();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();

        BlobId blobId = BlobId.of(firebaseProperties.getBucketName(), filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/"
                + firebaseProperties.getBucketName()
                + "/o/" + encodedFileName
                + "?alt=media";

        return imageUrl;
    }

    public void deleteImage(String filename) {
        BlobId blobId = BlobId.of(firebaseProperties.getBucketName(), filename);
        storage.delete(blobId);
    }

    // URL에서 filename 추출
    public String extractFilenameFromUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("/o/")) {
            throw new IllegalArgumentException("유효하지 않은 이미지 URL입니다.");
        }

        // "https://.../o/파일명?alt=media" → 파일명만 추출
        String[] parts = imageUrl.split("/o/");
        String encodedFilenameWithParams = parts[1];
        int endIndex = encodedFilenameWithParams.indexOf('?');
        if (endIndex != -1) {
            encodedFilenameWithParams = encodedFilenameWithParams.substring(0, endIndex);
        }

        // 인코딩 되어있는 파일명을 디코딩해서 GCS가 인식 가능한 원래 파일명으로 복원
        return URLDecoder.decode(encodedFilenameWithParams, StandardCharsets.UTF_8);
    }

}
