package com.example.toonieproject.service.Rental;


import com.example.toonieproject.dto.Rental.Cart.CartBookDto;
import com.example.toonieproject.dto.Rental.Cart.CartRequest;
import com.example.toonieproject.dto.Rental.Cart.CartResponse;
import com.example.toonieproject.entity.Book.Book;
import com.example.toonieproject.entity.Book.Series;
import com.example.toonieproject.entity.Rental.Cart;
import com.example.toonieproject.repository.Book.BookRepository;
import com.example.toonieproject.repository.Rental.CartRepository;
import com.example.toonieproject.util.auth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    public void addCartItems(CartRequest request) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(request.getUserId(), currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }

        for (long bookId : request.getBookId()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 bookId가 존재하지 않습니다: " + bookId));

            Cart cart = Cart.builder()
                    .userId(request.getUserId())
                    .storeId(request.getStoreId())
                    .book(book)
                    .addedAt(LocalDateTime.now())
                    .build();

            cartRepository.save(cart);
        }

    }


    public void removeCartItems(CartRequest request) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(request.getUserId(), currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }

        for (long bookId : request.getBookId()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 bookId가 존재하지 않습니다: " + bookId));

            Optional<Cart> optionalCart = cartRepository.findByUserIdAndStoreIdAndBook(request.getUserId(), request.getStoreId(), book);
            optionalCart.ifPresent(cartRepository::delete);
        }
    }

    public List<CartResponse> getCartBooksByUserAndRentable(Long userId, boolean isRentable) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(userId, currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }


        // 바로 조건에 맞는 Cart만 가져옴
        List<Cart> carts = cartRepository.findByUserIdAndBook_IsRentable(userId, isRentable);

        // storeId 기준으로 그룹화
        Map<Long, List<Cart>> groupedByStore = carts.stream()
                .collect(Collectors.groupingBy(Cart::getStoreId));

        List<CartResponse> result = new ArrayList<>();

        for (Map.Entry<Long, List<Cart>> entry : groupedByStore.entrySet()) {
            Long storeId = entry.getKey();
            List<Cart> storeCarts = entry.getValue();

            // storeName은 book에서 접근
            String storeName = storeCarts.get(0).getBook().getStore().getName();

            List<CartBookDto> bookDtos = storeCarts.stream()
                    .map(cart -> {
                        Book book = cart.getBook();
                        Series series = book.getSeries();
                        return CartBookDto.builder()
                                .bookId(book.getId())
                                .seriesName(series.getTitle())
                                .seriesImageUrl(series.getImage())
                                .rentalPrice(book.getRentalPrice())
                                .seriesNum(book.getSeriesNum())
                                .ageLimit(book.getAgeLimit())
                                .build();
                    })
                    .toList();

            result.add(CartResponse.builder()
                    .storeId(storeId)
                    .storeName(storeName)
                    .books(bookDtos)
                    .build());
        }

        return result;
    }




}
