package com.example.toonieproject.controller.Rental;

import com.example.toonieproject.dto.Rental.Reservation.AddRentalRequest;
import com.example.toonieproject.dto.Rental.Reservation.RentalByUserIdResponse;
import com.example.toonieproject.service.Rental.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rental")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/create")
    public ResponseEntity<String> createRental(@RequestBody AddRentalRequest request) throws AccessDeniedException {
        rentalService.createRental(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("대여 예약이 생성되었습니다.");
    }

    @GetMapping("/user/{userId}/lists")
    public ResponseEntity<Page<RentalByUserIdResponse>> getAllUserRentals(
            @PathVariable Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) throws AccessDeniedException {

        Pageable fixedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "reservedAt")
        );

        Page<RentalByUserIdResponse> result = rentalService.getReservationsByUserId(userId, fixedPageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{rentalId}")
    public ResponseEntity<RentalByUserIdResponse> getRentalDetail(@PathVariable Long rentalId) throws AccessDeniedException {
        RentalByUserIdResponse response = rentalService.getRentalDetailById(rentalId);
        return ResponseEntity.ok(response);
    }

    // 예약 취소처리하기
    @PatchMapping("/{rentalId}/cancel")
    public ResponseEntity<String> cancelRental(@PathVariable Long rentalId) throws AccessDeniedException {
        rentalService.cancelRentalByUser(rentalId);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }

    // 대여중으로 바꾸기


    // 반납 완료로 바꾸기



}
