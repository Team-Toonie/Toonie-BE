package com.example.toonieproject.dto.Rental.Reservation;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddRentalRequest {

    private Long userId;
    private Long storeId;

    private LocalDateTime scheduledRentDate; // 대여 예정일
    private LocalDateTime scheduledReturnDate; // 반납 예정일
    private LocalDateTime dueDate; // 대여 일수

    private int totalPrice;

    private String storeName;
    private String userName;
    private String userPhone;

    private Long[] bookIds;

}
