package com.example.toonieproject.controller.Rental;

import com.example.toonieproject.dto.Rental.Reservation.AddRentalRequest;
import com.example.toonieproject.dto.Rental.Reservation.RentalByUserIdResponse;
import com.example.toonieproject.service.Rental.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rental")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/create")
    public ResponseEntity<String> createRental(@RequestBody AddRentalRequest request) {
        rentalService.createRental(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("대여 예약이 생성되었습니다.");
    }

    @GetMapping("/user/{userId}/lists")
    public ResponseEntity<Page<RentalByUserIdResponse>> getAllUserRentals(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "reservedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RentalByUserIdResponse> result = rentalService.getReservationsByUserId(userId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{rentalId}")
    public ResponseEntity<RentalByUserIdResponse> getRentalDetail(@PathVariable Long rentalId) {
        RentalByUserIdResponse response = rentalService.getRentalDetailById(rentalId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{rentalId}/cancel")
    public ResponseEntity<String> cancelRental(@PathVariable Long rentalId) {
        rentalService.cancelRentalByUser(rentalId);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }



}
