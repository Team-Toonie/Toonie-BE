package com.example.toonieproject.controller.Book;


import com.example.toonieproject.dto.Series.AddSeriesOfStoreRequest;
import com.example.toonieproject.dto.Series.SeriesOfStoreDetailResponse;
import com.example.toonieproject.service.Book.SeriesOfStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class SeriesOfStoreController {

    private final SeriesOfStoreService seriesOfStoreService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/stores/{storeId}/series/{seriesId}")
    public ResponseEntity<String> addSeriesOfStore(@RequestBody AddSeriesOfStoreRequest request,
                                                   @PathVariable long storeId,
                                                   @PathVariable long seriesId) {

        seriesOfStoreService.add(request, storeId, seriesId);


        return ResponseEntity.status(HttpStatus.CREATED).body("seriesOfStore created successfully");

    }

    @GetMapping("/stores/{storeId}/series/{seriesId}")
    public ResponseEntity<SeriesOfStoreDetailResponse> getSeriesOfStoreDetails(@PathVariable Long storeId,
                                                                         @PathVariable Long seriesId) {

        return ResponseEntity.ok(seriesOfStoreService.getSeriesOfStoreDetails(storeId, seriesId));

    }



}
