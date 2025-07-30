package com.example.toonieproject.entity.Rental;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rental_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalItemId;

    private Long rentalId;

    private Long bookId;

    private int price;

    // 스냅샷 정보
    private String bookTitle;
    private String bookImageUrl;
}
