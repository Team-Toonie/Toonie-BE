package com.example.toonieproject.controller.Book;


import com.example.toonieproject.dto.Series.AddSeriesOfStoreRequest;
import com.example.toonieproject.service.Book.SeriesOfStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


}
