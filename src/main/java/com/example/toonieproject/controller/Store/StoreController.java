package com.example.toonieproject.controller.Store;


import com.example.toonieproject.dto.Store.AddStoreRequest;
import com.example.toonieproject.dto.Store.OwnerStoresResponse;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.service.Store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/add")
    public ResponseEntity<String> addStore(@RequestBody AddStoreRequest request) {

        Store store = storeService.add(request);

        // 리다이렉트 추가
//        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
//                .location(URI.create("/stores/{storeId}")) // 리다이렉트할 경로
//                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body("Store created successfully");

    }

    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<List<OwnerStoresResponse>> findByOwnerId(@PathVariable long ownerId) {

        return ResponseEntity.ok(storeService.findByOwnerId(ownerId));
    }



}
