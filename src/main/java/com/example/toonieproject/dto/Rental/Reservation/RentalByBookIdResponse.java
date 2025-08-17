package com.example.toonieproject.dto.Rental.Reservation;

import com.example.toonieproject.entity.Rental.Rental.RentalStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalByBookIdResponse {
    private Long rentalId;
    private String userName;
    private LocalDateTime reservedAt;
    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
    private RentalStatus rentalStatus;
    private boolean overdue;
}