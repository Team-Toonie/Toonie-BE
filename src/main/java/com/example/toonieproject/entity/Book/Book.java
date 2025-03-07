package com.example.toonieproject.entity.Book;

import com.example.toonieproject.entity.Store.Store;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id; // 책 ID

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false)
    private Series series; // 시리즈 (외래키)

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 가게 (외래키)

    @Column(name = "rental_price", nullable = false)
    private Integer rentalPrice; // 대여 가격

    @Column(name = "series_num", nullable = false)
    private Integer seriesNum; // 시리즈 내 번호

    @Column(name = "is_rentable", nullable = false)
    private Boolean isRentable; // 대여 가능 여부

    @Column(name = "age_limit", nullable = false)
    private Integer ageLimit; // 연령 제한
}
