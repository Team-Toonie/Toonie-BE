package com.example.toonieproject.controller.Store;


import com.example.toonieproject.dto.Store.AddStoreRequest;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.service.Store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/stores/add")
    public ResponseEntity<String> addStore(@RequestBody AddStoreRequest request) {

        Store store = storeService.add(request);

        // 리다이렉트 추가
//        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
//                .location(URI.create("/stores/{storeId}")) // 리다이렉트할 경로
//                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body("Store created successfully");

    }



}
