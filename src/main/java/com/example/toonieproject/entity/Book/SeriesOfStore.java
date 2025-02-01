package com.example.toonieproject.entity.Book;


import com.example.toonieproject.entity.Store.Store;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seriesOfStore")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SeriesOfStore {

    @EmbeddedId
    private SeriesOfStoreId id; // 복합 키

    @ManyToOne
    @MapsId("seriesId")
    @JoinColumn(name = "series_id", nullable = false)
    private Series series; // 시리즈 (외래키)

    @ManyToOne
    @MapsId("storeId")
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 가게 (외래키)

    @Column(name = "max_of_rental_period", nullable = false)
    private Integer maxOfRentalPeriod; // 대여 가능 최대 기간

    @Column(nullable = false)
    private Integer volume; // 보유 중인 권 수
}