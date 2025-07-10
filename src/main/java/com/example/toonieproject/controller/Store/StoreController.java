package com.example.toonieproject.controller.Store;


import com.example.toonieproject.dto.Store.*;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.service.Store.MapService;
import com.example.toonieproject.service.Store.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;
    private final MapService mapService;

//    @PreAuthorize("hasRole('OWNER')")
//    @PostMapping("/add")
//    public ResponseEntity<String> addStore(
//            @RequestPart("request") AddStoreRequest request,
//            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
//
//        Store store = storeService.add(request, imageFile);
//
//        // 리다이렉트 추가
////        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
////                .location(URI.create("/stores/{storeId}")) // 리다이렉트할 경로
////                .build();
//
//        return ResponseEntity.status(HttpStatus.CREATED).body("Store created successfully");
//
//    }
    @Operation(summary = "가게 등록!", description = "가게를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "가게 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> addStore(

            @Parameter(
                    description = "가게 등록 요청 정보(JSON)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddStoreRequest.class)
                    )
            )
            @RequestPart("request") AddStoreRequest request,

            @Parameter(description = "가게 이미지 파일")
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        Store store = storeService.add(request, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body("Store created successfully");
    }



    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<List<OwnerStoresResponse>> findByOwnerId(@PathVariable long ownerId) {

        return ResponseEntity.ok(storeService.findByUserId(ownerId));
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

    @GetMapping("/search")
    public ResponseEntity<List<StoreSearchResponse>> searchStores(
            @RequestParam String query
    ) {

        List<StoreSearchResponse> stores = storeService.searchStores(query);

        return ResponseEntity.ok(stores);
    }



}
