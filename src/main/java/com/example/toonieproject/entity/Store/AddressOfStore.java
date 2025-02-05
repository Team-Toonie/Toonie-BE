package com.example.toonieproject.entity.Store;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "address_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressOfStore {
    @Id
    @Column(name = "store_id")
    private Long storeId; // 기본키이면서 외래키

    @OneToOne
    @MapsId
    @JoinColumn(name = "store_id")
    private Store store; // Store 테이블과 1:1 관계

    @Column(name = "address", nullable = false, length = 255)
    private String address; // 기본 주소

    @Column(name = "detailed_address", length = 255)
    private String detailedAddress; // 상세 주소

    @Column(name = "lat", precision = 10, scale = 7, nullable = false)
    private BigDecimal lat; // 위도

    @Column(name = "lng", precision = 10, scale = 7, nullable = false)
    private BigDecimal lng; // 경도


}
