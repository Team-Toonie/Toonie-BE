package com.example.toonieproject.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "owner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long id; // 소유자 ID

    @Column(nullable = false, length = 100)
    private String name; // 이름

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber; // 전화번호

    @Column(name = "email_address", nullable = false, length = 255)
    private String emailAddress; // 이메일 주소
}
