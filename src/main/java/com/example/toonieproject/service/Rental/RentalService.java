package com.example.toonieproject.service.Rental;

import com.example.toonieproject.dto.Rental.Reservation.AddRentalRequest;
import com.example.toonieproject.dto.Rental.Reservation.RentalItemDto;
import com.example.toonieproject.dto.Rental.Reservation.RentalByUserIdResponse;
import com.example.toonieproject.entity.Book.Book;
import com.example.toonieproject.entity.Book.Series;
import com.example.toonieproject.entity.Rental.Rental;
import com.example.toonieproject.entity.Rental.RentalItem;
import com.example.toonieproject.repository.Book.BookRepository;
import com.example.toonieproject.repository.Rental.RentalItemRepository;
import com.example.toonieproject.repository.Rental.RentalRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import com.example.toonieproject.util.auth.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final RentalItemRepository rentalItemRepository;
    private final BookRepository bookRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void createRental(AddRentalRequest request) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(request.getUserId(), currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }


        // 1. Rental 저장
        Rental rental = Rental.builder()
                .userId(request.getUserId())
                .storeId(request.getStoreId())
                .scheduledRentDate(request.getScheduledRentDate())
                .scheduledReturnDate(request.getScheduledReturnDate())
                .dueDate(request.getDueDate())
                .totalPrice(request.getTotalPrice())
                .storeName(request.getStoreName())
                .userName(request.getUserName())
                .userPhone(request.getUserPhone())
                .reservedAt(LocalDateTime.now())
                .rentalStatus(Rental.RentalStatus.RESERVED)
                .build();

        rentalRepository.save(rental);

        // 2. RentalItem 저장
        for (Long bookId : request.getBookIds()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 bookId 없음: " + bookId));
            Series series = book.getSeries();

            book.setIsRentable(false);  // 대여 중 상태로 변경

            RentalItem item = RentalItem.builder()
                    .rentalId(rental.getRentalId())
                    .bookId(bookId)
                    .price(book.getRentalPrice())
                    .bookTitle(series.getTitle())
                    .bookImageUrl(series.getImage())
                    .bookNum(book.getSeriesNum())
                    .build();

            rentalItemRepository.save(item);
        }
    }


    public Page<RentalByUserIdResponse> getReservationsByUserId(Long userId, Pageable pageable) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(userId, currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }


        Page<Rental> rentals = rentalRepository.findByUserIdOrderByReservedAtDesc(userId, pageable);

        return rentals.map(rental -> {
            List<RentalItem> items = rentalItemRepository.findByRentalId(rental.getRentalId());
            List<RentalItemDto> bookDtos = items.stream()
                    .map(item -> RentalItemDto.builder()
                            .bookId(item.getBookId())
                            .bookTitle(item.getBookTitle())
                            .bookImageUrl(item.getBookImageUrl())
                            .bookNum(item.getBookNum())
                            .price(item.getPrice())
                            .build())
                    .toList();


            boolean storeExists = storeRepository.existsById(rental.getStoreId());
            boolean isOverdue = false;
            if (rental.getRentalStatus() == Rental.RentalStatus.RENTED) {
                isOverdue = rental.getDueDate().isBefore(LocalDateTime.now());
            }

            return RentalByUserIdResponse.builder()
                    .rentalId(rental.getRentalId())
                    .storeExists(storeExists)
                    .storeName(rental.getStoreName())
                    .userName(rental.getUserName())
                    .userPhone(rental.getUserPhone())
                    .scheduledRentDate(rental.getScheduledRentDate())
                    .scheduledReturnDate(rental.getScheduledReturnDate())
                    .dueDate(rental.getDueDate())
                    .reservedAt(rental.getReservedAt())
                    .totalPrice(rental.getTotalPrice())
                    .rentalStatus(rental.getRentalStatus())
                    .books(bookDtos)
                    .overdue(isOverdue)
                    .build();
        });
    }


    public RentalByUserIdResponse getRentalDetailById(Long rentalId) throws AccessDeniedException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 rentalId가 존재하지 않습니다: " + rentalId));

        // 회원아이디 일치하는지 검증
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(rental.getUserId(), currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }


        List<RentalItem> items = rentalItemRepository.findByRentalId(rentalId);

        List<RentalItemDto> bookDtos = items.stream()
                .map(item -> RentalItemDto.builder()
                        .bookId(item.getBookId())
                        .bookTitle(item.getBookTitle())
                        .bookImageUrl(item.getBookImageUrl())
                        .bookNum(item.getBookNum())
                        .price(item.getPrice())
                        .build())
                .toList();

        boolean storeExists = storeRepository.existsById(rental.getStoreId());
        boolean isOverdue = false;
        if (rental.getRentalStatus() == Rental.RentalStatus.RENTED) {
            isOverdue = rental.getDueDate().isBefore(LocalDateTime.now());
        }

        return RentalByUserIdResponse.builder()
                .rentalId(rental.getRentalId())
                .storeExists(storeExists)
                .storeName(rental.getStoreName())
                .userName(rental.getUserName())
                .userPhone(rental.getUserPhone())
                .scheduledRentDate(rental.getScheduledRentDate())
                .scheduledReturnDate(rental.getScheduledReturnDate())
                .dueDate(rental.getDueDate())
                .reservedAt(rental.getReservedAt())
                .totalPrice(rental.getTotalPrice())
                .rentalStatus(rental.getRentalStatus())
                .books(bookDtos)
                .overdue(isOverdue)
                .build();
    }


    @Transactional
    public void cancelRentalByUser(Long rentalId) throws AccessDeniedException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 rentalId가 존재하지 않습니다: " + rentalId));

        // user id 검증
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (!Objects.equals(rental.getUserId(), currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }


        if (rental.getRentalStatus() != Rental.RentalStatus.RESERVED) {
            throw new IllegalStateException("이미 대여가 시작되었거나 취소된 예약은 취소할 수 없습니다.");
        }

        rental.setRentalStatus(Rental.RentalStatus.CANCELED);
        rental.setCanceledAt(LocalDateTime.now());

        List<RentalItem> items = rentalItemRepository.findByRentalId(rentalId);
        for (RentalItem item : items) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("book 없음"));
            book.setIsRentable(true);  // 다시 대여 가능으로
        }
    }



}
