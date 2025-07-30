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
    private RentalStatus rentalStatus;

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
        예약_중,
        예약_취소,
        대여_중,
        반납_완료
    }
}
