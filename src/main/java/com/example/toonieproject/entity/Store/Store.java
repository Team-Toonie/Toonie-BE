package com.example.toonieproject.entity.Store;

import com.example.toonieproject.entity.User.Owner;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id; // 가게 ID

    @Column(nullable = false, length = 100)
    private String name; // 가게 이름

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber; // 전화번호

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner; // 소유자 (외래키)

    @Column(name = "represent_url", length = 255)
    private String representUrl; // 대표 URL

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true; // 상태 (true = 영업 중, false = 폐업)

    @Column(name = "info", nullable = true)
    private String info;
}

