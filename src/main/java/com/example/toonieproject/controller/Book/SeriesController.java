package com.example.toonieproject.controller.Book;


import com.example.toonieproject.dto.Series.AddSeriesRequest;
import com.example.toonieproject.dto.Series.SeriesDetailResponse;
import com.example.toonieproject.dto.Store.AddStoreRequest;
import com.example.toonieproject.dto.Store.StoreFindBySeriesResponse;
import com.example.toonieproject.service.Book.SeriesService;
import com.example.toonieproject.service.Store.MapService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/series")
public class SeriesController {

    // 서비스 추가
    private final SeriesService seriesService;
    private final MapService mapService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addSeries(
            @Parameter(
                    description = "시리즈 등록 요청 정보(JSON)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddStoreRequest.class)
                    )
            )
            @RequestBody AddSeriesRequest request,

            @Parameter(description = "가게 이미지 파일")
            @RequestPart(value = "image", required = false) MultipartFile imageFile
            ) {

        seriesService.add(request, imageFile);

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

    // 가게가 보유한 시리즈 목록
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<SeriesDetailResponse>> getSeriesByStore(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SeriesDetailResponse> result = seriesService.getSeriesByStore(storeId, pageable);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/all")
    public ResponseEntity<Page<SeriesDetailResponse>> getSeriesAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<SeriesDetailResponse> result = seriesService.getSeriesAll(pageable);

        return ResponseEntity.ok(result);
    }


}
