package com.example.toonieproject.controller.Book;

import com.example.toonieproject.dto.Book.AddBookRequest;
import com.example.toonieproject.service.Book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    @PostMapping("/stores/{storeId}/books/add")
    public ResponseEntity<String> addBook(@RequestBody AddBookRequest request,
                                          @PathVariable long storeId) {

        bookService.add(request, storeId);


        return ResponseEntity.status(HttpStatus.CREATED).body("book created successfully");
    }


}
