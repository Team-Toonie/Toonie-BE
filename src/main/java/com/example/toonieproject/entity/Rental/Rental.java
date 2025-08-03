package com.example.toonieproject.entity.Rental;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    private Long userId;

    private Long storeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status", nullable = false)
    private RentalStatus rentalStatus;

    @Column(name = "scheduled_rent_date")
    private LocalDateTime scheduledRentDate;
    @Column(name = "scheduled_return_date")
    private LocalDateTime scheduledReturnDate;

    private LocalDateTime reservedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
    private LocalDateTime dueDate;

    private int totalPrice;

    // 스냅샷 정보
    private String storeName;
    private String userName;
    private String userPhone;

    public enum RentalStatus {
        RESERVED,
        CANCELED,
        RENTED,
        RETURNED
    }
}
