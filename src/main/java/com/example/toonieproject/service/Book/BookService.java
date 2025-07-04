package com.example.toonieproject.service.Book;


import com.example.toonieproject.dto.Book.AddBookRequest;
import com.example.toonieproject.dto.Book.AddSingleBookRequest;
import com.example.toonieproject.dto.Book.SingleBookDetailResponse;
import com.example.toonieproject.entity.Book.Book;
import com.example.toonieproject.entity.Book.Series;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Book.BookRepository;
import com.example.toonieproject.repository.Book.SeriesRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final SeriesRepository seriesRepository;
    private final StoreRepository storeRepository;


    public void add(AddSingleBookRequest addSingleBookRequest, long storeId) {


        Series series = seriesRepository.findById(addSingleBookRequest.getSeriesId())
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + addSingleBookRequest.getSeriesId()));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + storeId));


        Book book = new Book();
        book.setSeries(series);
        book.setStore(store);
        book.setSeriesNum(addSingleBookRequest.getSeriesNum());
        book.setRentalPrice(addSingleBookRequest.getRentalPrice());
        book.setIsRentable(addSingleBookRequest.getIsRentable());
        book.setAgeLimit(addSingleBookRequest.getAgeLimit());

        bookRepository.save(book);
    }

    public SingleBookDetailResponse getBookDetail(Long bookId) {

        Book result = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                "Book not found with bookId " + bookId
        ));

        return new SingleBookDetailResponse(result);
    }




}
