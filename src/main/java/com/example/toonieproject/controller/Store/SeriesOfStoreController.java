package com.example.toonieproject.controller.Store;


import com.example.toonieproject.dto.Series.AddSeriesOfStoreRequest;
import com.example.toonieproject.service.Store.SeriesOfStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SeriesOfStoreController {

    private final SeriesOfStoreService seriesOfStoreService;

    @PostMapping("/stores/{storeId}/series/{seriesId}")
    public ResponseEntity<String> addSeriesOfStore(@RequestBody AddSeriesOfStoreRequest request,
                                                   @PathVariable long storeId,
                                                   @PathVariable long seriesId) {

        seriesOfStoreService.add(request, storeId, seriesId);


        return ResponseEntity.status(HttpStatus.CREATED).body("seriesOfStore created successfully");

    }


}
