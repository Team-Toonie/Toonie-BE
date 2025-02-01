package com.example.toonieproject.service.Book;


import com.example.toonieproject.dto.Book.AddBookRequest;
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



    public void add(AddBookRequest addBookRequest, long storeId) {


        Series series = seriesRepository.findById(addBookRequest.getSeriesId())
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + addBookRequest.getSeriesId()));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + storeId));


        Book book = new Book();
        book.setSeries(series);
        book.setStore(store);
        book.setSeriesNum(addBookRequest.getSeriesNum());
        book.setRentalPrice(addBookRequest.getRentalPrice());
        book.setIsRentable(addBookRequest.getIsRentable());
        book.setAgeLimit(addBookRequest.getAgeLimit());

        bookRepository.save(book);



    }




}
