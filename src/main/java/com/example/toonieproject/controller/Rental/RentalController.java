package com.example.toonieproject.controller.Rental;

import com.example.toonieproject.dto.Rental.Reservation.AddRentalRequest;
import com.example.toonieproject.dto.Rental.Reservation.RentalByBookIdResponse;
import com.example.toonieproject.dto.Rental.Reservation.RentalDetailResponse;
import com.example.toonieproject.service.Rental.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rental")
public class RentalController {

    private final RentalService rentalService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<String> createRental(@RequestBody AddRentalRequest request) throws AccessDeniedException {
        rentalService.createRental(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("대여 예약이 생성되었습니다.");
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/user/{userId}/lists")
    public ResponseEntity<Page<RentalDetailResponse>> getAllUserRentals(
            @PathVariable Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) throws AccessDeniedException {

        Pageable fixedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "reservedAt")
        );

        Page<RentalDetailResponse> result = rentalService.getRentalHistoryByUserId(userId, fixedPageable);
        return ResponseEntity.ok(result);
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/book/{bookId}/lists")
    public ResponseEntity<Page<RentalByBookIdResponse>> getRentalHistoryByBookId(
            @PathVariable Long bookId,
            @PageableDefault(size = 10) Pageable pageable
    ) throws AccessDeniedException {

        Pageable fixedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "reservedAt")  // reservedAt 기준 내림차순
        );

        Page<RentalByBookIdResponse> responses = rentalService.getRentalHistoryByBookId(bookId, fixedPageable);
        return ResponseEntity.ok(responses);
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/store/{storeId}/lists")
    public ResponseEntity<Page<RentalDetailResponse>> getRentalHistoryByStoreId(
            @PathVariable Long storeId,
            @PageableDefault(size = 10) Pageable pageable
    ) throws AccessDeniedException {

        Pageable fixedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "reservedAt")
        );

        Page<RentalDetailResponse> responses = rentalService.getRentalHistoryByStoreId(storeId, fixedPageable);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{rentalId}")
    public ResponseEntity<RentalDetailResponse> getRentalDetail(@PathVariable Long rentalId) throws AccessDeniedException {
        RentalDetailResponse response = rentalService.getRentalDetailById(rentalId);
        return ResponseEntity.ok(response);
    }

    // 예약 취소처리하기
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{rentalId}/cancel")
    public ResponseEntity<String> cancelRental(@PathVariable Long rentalId) throws AccessDeniedException {
        rentalService.cancelRentalByUser(rentalId);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }

    // 대여중으로 바꾸기
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{rentalId}/rent")
    public ResponseEntity<String> startRental(@PathVariable Long rentalId) throws AccessDeniedException {
        rentalService.startRental(rentalId);
        return ResponseEntity.ok("대여가 시작되었습니다.");
    }


    // 반납 완료로 바꾸기
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{rentalId}/return")
    public ResponseEntity<String> completeReturn(@PathVariable Long rentalId) throws AccessDeniedException {
        rentalService.completeReturn(rentalId);
        return ResponseEntity.ok("반납이 완료되었습니다.");
    }



}
