package com.example.toonieproject.controller.Book;


import com.example.toonieproject.dto.Series.AddSeriesRequest;
import com.example.toonieproject.service.Book.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SeriesController {

    // 서비스 추가
    private final SeriesService seriesService;

    @PostMapping("/series/register")
    public ResponseEntity<String> addSeries(@RequestBody AddSeriesRequest request) {

        seriesService.add(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Series created successfully");

    }


}
