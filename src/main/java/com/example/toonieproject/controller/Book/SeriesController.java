package com.example.toonieproject.controller.Book;


import com.example.toonieproject.dto.Series.AddSeriesRequest;
import com.example.toonieproject.dto.Series.SeriesDetailResponse;
import com.example.toonieproject.dto.Store.StoreFindBySeriesResponse;
import com.example.toonieproject.service.Book.SeriesService;
import com.example.toonieproject.service.Store.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/series")
public class SeriesController {

    // 서비스 추가
    private final SeriesService seriesService;
    private final MapService mapService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/register")
    public ResponseEntity<String> addSeries(@RequestBody AddSeriesRequest request) {

        seriesService.add(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Series created successfully");

    }

    @GetMapping("/{seriesId}")
    public ResponseEntity<SeriesDetailResponse> getSeriesDetails(@PathVariable Long seriesId) {

        return ResponseEntity.ok(seriesService.getSeriesDetails(seriesId));
    }

    @GetMapping("/{seriesId}/stores")
    public ResponseEntity<List<StoreFindBySeriesResponse>> getStoresBySeries(
            @PathVariable Long seriesId,
            @RequestParam double lat,
            @RequestParam double lon) {

        List<StoreFindBySeriesResponse> response = mapService.getStoresBySeriesId(seriesId, lat, lon);
        return ResponseEntity.ok(response);
    }
}
