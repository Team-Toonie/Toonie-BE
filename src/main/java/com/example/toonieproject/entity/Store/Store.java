package com.example.toonieproject.entity.Store;

import com.example.toonieproject.entity.User.User;
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
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 소유자 (외래키)

    @Column(name = "represent_url", length = 255)
    private String representUrl; // 대표 URL

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true; // 상태 (true = 영업 중, false = 폐업)

    @Column(name = "info", nullable = true)
    private String info;

    @Column(name = "image")
    private String image;



    @OneToOne(mappedBy = "store", fetch = FetchType.LAZY) //cascade = CascadeType.ALL(주소 테이블에 자동저장) 삭제(기존 로직 유지를 위해)
    private AddressOfStore addressInfo; // 주소 정보


}

