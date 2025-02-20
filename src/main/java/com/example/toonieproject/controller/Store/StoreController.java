package com.example.toonieproject.controller.Store;


import com.example.toonieproject.dto.Store.*;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.service.Store.MapService;
import com.example.toonieproject.service.Store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stores")
//@CrossOrigin(origins = "http://localhost:5173")
public class StoreController {

    private final StoreService storeService;
    private final MapService mapService;

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

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreViewResponse> findByStoreId(@PathVariable long storeId) {

        return ResponseEntity.ok(storeService.findByStoreId(storeId));
    }


    @GetMapping("/nearby")
    public ResponseEntity<List<StoreNearbyResponse>> getNearbyStores(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "1") int page
    ) {
        List<StoreNearbyResponse> stores = mapService.findNearbyStores(lat, lng, page);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/in-bounds")
    public ResponseEntity<List<StoreMapResponse>> getStoresInBounds(
            @RequestParam BigDecimal minLat,
            @RequestParam BigDecimal maxLat,
            @RequestParam BigDecimal minLng,
            @RequestParam BigDecimal maxLng
    ) {
        List<StoreMapResponse> stores = mapService.findStoresInBounds(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(stores);
    }



}
