package com.example.toonieproject.controller.Book;

import com.example.toonieproject.dto.Book.AddBookRequest;
import com.example.toonieproject.dto.Book.AddSingleBookRequest;
import com.example.toonieproject.dto.Book.BookWithCartStatusResponse;
import com.example.toonieproject.dto.Book.SingleBookDetailResponse;
import com.example.toonieproject.service.Book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/add")
    public ResponseEntity<String> addBook(@RequestBody AddBookRequest request) throws AccessDeniedException {

        for (Integer num : request.getSeriesNum()) {
            AddSingleBookRequest singleRequest = new AddSingleBookRequest();

            singleRequest.setSeriesId(request.getSeriesId());
            singleRequest.setStoreId(request.getStoreId());
            singleRequest.setRentalPrice(request.getRentalPrice());
            singleRequest.setSeriesNum(num); // 단일 번호 배열로
            singleRequest.setIsRentable(request.getIsRentable());
            singleRequest.setAgeLimit(request.getAgeLimit());

            bookService.add(singleRequest);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("book created successfully");
    }


    @GetMapping("/{bookId}")
    public ResponseEntity<SingleBookDetailResponse> getBookDetail(@PathVariable Long bookId) {

        return ResponseEntity.ok(bookService.getBookDetail(bookId));
    }


    @GetMapping("/series/{seriesId}/store/{storeId}/user/{userId}")
    public ResponseEntity<List<BookWithCartStatusResponse>> getBooksWithCartStatus(
            @PathVariable Long seriesId,
            @PathVariable Long storeId,
            @PathVariable Long userId
    ) throws AccessDeniedException {
        List<BookWithCartStatusResponse> result =
                bookService.getBooksWithCartStatus(seriesId, storeId, userId);
        return ResponseEntity.ok(result);
    }


}
