package com.example.toonieproject.service.Rental;

import com.example.toonieproject.dto.Rental.Reservation.AddRentalRequest;
import com.example.toonieproject.dto.Rental.Reservation.RentalByBookIdResponse;
import com.example.toonieproject.dto.Rental.Reservation.RentalItemDto;
import com.example.toonieproject.dto.Rental.Reservation.RentalByUserIdResponse;
import com.example.toonieproject.entity.Book.Book;
import com.example.toonieproject.entity.Book.Series;
import com.example.toonieproject.entity.Rental.Rental;
import com.example.toonieproject.entity.Rental.RentalItem;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Book.BookRepository;
import com.example.toonieproject.repository.Rental.RentalItemRepository;
import com.example.toonieproject.repository.Rental.RentalRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import com.example.toonieproject.util.auth.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Comparator;
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
        String currentUserRole = SecurityUtil.getCurrentUserRole();

        // 인가 검증
        if ("CUSTOMER".equals(currentUserRole)) {
            // 일반 사용자: 자기 userId만 예약 가능
            if (!Objects.equals(request.getUserId(), currentUserId)) {
                throw new AccessDeniedException("You do not have permission to make a reservation.");
            }
        } else if ("OWNER".equals(currentUserRole)) {
            // 점주: 자기 가게에 대해서만 예약 생성 가능
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 storeId가 존재하지 않습니다."));

            if (!Objects.equals(store.getUser().getId(), currentUserId)) {
                throw new AccessDeniedException("You do not have permission to manage this store.");
            }
        } else {
            throw new AccessDeniedException("Invalid role.");
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


    @Transactional(readOnly = true)
    public Page<RentalByBookIdResponse> getRentalHistoryByBookId(Long bookId, Pageable pageable) throws AccessDeniedException {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 bookId가 존재하지 않습니다."));

        // 가게 소유주 확인
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Store store = book.getStore();
        if (!Objects.equals(store.getUser().getId(), currentUserId)) {
            throw new AccessDeniedException("해당 가게에 대한 접근 권한이 없습니다.");
        }

        // rental_item에서 rentalId 모음
        List<Long> rentalIds = rentalItemRepository.findByBookId(bookId).stream()
                .map(RentalItem::getRentalId)
                .distinct()
                .toList();

        // rentalId 기준 페이징
        Page<Rental> rentals = rentalRepository.findByRentalIdIn(rentalIds, pageable);

        return rentals.map(rental -> RentalByBookIdResponse.builder()
                .rentalId(rental.getRentalId())
                .userName(rental.getUserName())
                .reservedAt(rental.getReservedAt())
                .rentedAt(rental.getRentedAt())
                .returnedAt(rental.getReturnedAt())
                .rentalStatus(rental.getRentalStatus())
                .overdue(rental.getRentalStatus() == Rental.RentalStatus.RENTED &&
                        rental.getDueDate() != null &&
                        rental.getDueDate().isBefore(LocalDateTime.now()))
                .build());
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


    // 렌탈 상태 변경

    @Transactional
    public void cancelRentalByUser(Long rentalId) throws AccessDeniedException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 rentalId가 존재하지 않습니다: " + rentalId));

        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserRole = SecurityUtil.getCurrentUserRole();

        // 인가 검증
        if ("CUSTOMER".equals(currentUserRole)) {
            // 일반 사용자: 자기 userId만 예약 취소 가능
            if (!Objects.equals(rental.getUserId(), currentUserId)) {
                throw new AccessDeniedException("You do not have permission to make a reservation.");
            }
        } else if ("OWNER".equals(currentUserRole)) {
            // 점주: 자기 가게에 대해서만 예약 취소 가능
            Store store = storeRepository.findById(rental.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 storeId가 존재하지 않습니다."));

            if (!Objects.equals(store.getUser().getId(), currentUserId)) {
                throw new AccessDeniedException("You do not have permission to manage this store.");
            }
        } else {
            throw new AccessDeniedException("Invalid role.");
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

    @Transactional
    public void startRental(Long rentalId) throws AccessDeniedException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 rentalId가 존재하지 않습니다."));

        // 권한 검증
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Store store = storeRepository.findById(rental.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("해당 storeId가 존재하지 않습니다."));

        if (!Objects.equals(store.getUser().getId(), currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }

        if (rental.getRentalStatus() != Rental.RentalStatus.RESERVED) {
            throw new IllegalStateException("예약 상태가 아니면 대여를 시작할 수 없습니다.");
        }

        // 상태 전환
        rental.setRentalStatus(Rental.RentalStatus.RENTED);
        rental.setRentedAt(LocalDateTime.now());

        // 책들 대여 불가 처리
        List<RentalItem> items = rentalItemRepository.findByRentalId(rentalId);
        for (RentalItem item : items) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다."));
            book.setIsRentable(false);
        }
    }

    @Transactional
    public void completeReturn(Long rentalId) throws AccessDeniedException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 rentalId가 존재하지 않습니다."));

        // 권한 검증
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Store store = storeRepository.findById(rental.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("해당 storeId가 존재하지 않습니다."));

        if (!Objects.equals(store.getUser().getId(), currentUserId)){
            throw new AccessDeniedException("You do not have permission.");
        }

        rental.setRentalStatus(Rental.RentalStatus.RETURNED);
        rental.setReturnedAt(LocalDateTime.now());

        // 책들 대여 가능 상태로 되돌림
        List<RentalItem> items = rentalItemRepository.findByRentalId(rentalId);
        for (RentalItem item : items) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다."));
            book.setIsRentable(true);
        }
    }







}
