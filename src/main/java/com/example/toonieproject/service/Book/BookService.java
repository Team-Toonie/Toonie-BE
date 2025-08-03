package com.example.toonieproject.service.Book;


import com.example.toonieproject.dto.Book.AddBookRequest;
import com.example.toonieproject.dto.Book.AddSingleBookRequest;
import com.example.toonieproject.dto.Book.BookWithCartStatusResponse;
import com.example.toonieproject.dto.Book.SingleBookDetailResponse;
import com.example.toonieproject.entity.Book.Book;
import com.example.toonieproject.entity.Book.Series;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Book.BookRepository;
import com.example.toonieproject.repository.Book.SeriesRepository;
import com.example.toonieproject.repository.Rental.CartRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import com.example.toonieproject.util.auth.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final SeriesRepository seriesRepository;
    private final StoreRepository storeRepository;
    private final SeriesOfStoreService seriesOfStoreService;
    private final CartRepository cartRepository;


    public void add(AddSingleBookRequest addSingleBookRequest) throws AccessDeniedException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        // storeId로 Store 가져오기
        Store store = storeRepository.findById(addSingleBookRequest.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        // ownerId 검증
        if (!store.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }

        Series series = seriesRepository.findById(addSingleBookRequest.getSeriesId())
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + addSingleBookRequest.getSeriesId()));
        store = storeRepository.findById(addSingleBookRequest.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + addSingleBookRequest.getStoreId()));

        Book book = new Book();
        book.setSeries(series);
        book.setStore(store);
        book.setSeriesNum(addSingleBookRequest.getSeriesNum());
        book.setRentalPrice(addSingleBookRequest.getRentalPrice());
        book.setIsRentable(addSingleBookRequest.getIsRentable());
        book.setAgeLimit(addSingleBookRequest.getAgeLimit());

        bookRepository.save(book);
        seriesOfStoreService.updateVolumeByBook(book.getStore().getId(), book.getSeries().getSeriesId());
    }

    public SingleBookDetailResponse getBookDetail(Long bookId) {

        Book result = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                "Book not found with bookId " + bookId
        ));

        return new SingleBookDetailResponse(result);
    }


    public List<BookWithCartStatusResponse> getBooksWithCartStatus(Long seriesId, Long storeId, Long userId) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(userId, currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }

        List<Book> books = bookRepository.findBySeries_SeriesIdAndStore_Id(seriesId, storeId);
        List<Long> userCartBookIds = cartRepository.findBookIdsByUserId(userId);

        return books.stream()
                .map(book -> BookWithCartStatusResponse.builder()
                        .bookId(book.getId())
                        .seriesNum(book.getSeriesNum())
                        .rentalPrice(book.getRentalPrice())
                        .isRentable(book.getIsRentable())
                        .ageLimit(book.getAgeLimit())
                        .inCart(userCartBookIds.contains(book.getId()))
                        .build())
                .toList();
    }




}
