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
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(firebaseProperties.getBucketName(), filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        // GCS의 공개 URL (Firebase의 Storage에서 사용)
        return String.format("https://storage.googleapis.com/%s/%s", firebaseProperties.getBucketName(), filename);
    }

}
