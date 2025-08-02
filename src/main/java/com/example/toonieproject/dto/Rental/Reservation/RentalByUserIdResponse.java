package com.example.toonieproject.dto.Rental.Reservation;

import com.example.toonieproject.entity.Rental.Rental;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalByUserIdResponse {
        private Long rentalId;

        private String storeName;
        private String userName;
        private String userPhone;

        private LocalDateTime scheduledRentDate;
        private LocalDateTime scheduledReturnDate;
        private LocalDateTime dueDate;
        private LocalDateTime reservedAt;

        private int totalPrice;
        private Rental.RentalStatus rentalStatus;

        private List<RentalItemDto> books;
}
