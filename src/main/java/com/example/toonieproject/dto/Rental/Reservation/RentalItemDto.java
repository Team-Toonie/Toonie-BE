package com.example.toonieproject.dto.Rental.Reservation;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalItemDto {
    private Long bookId;
    private String bookTitle;
    private String bookImageUrl;
    private int bookNum;

    private int price;
}
