package com.example.toonieproject.entity.Auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // ID

    @Column(nullable = false, length = 100)
    private String name; // 이름

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email; // 이메일 주소 (unique 설정 추가)

    @Column(name = "phone_number", length = 15)
    private String phoneNumber; // 전화번호

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Role role; // 역할 (OWNER, CUSTOMER)

    public enum Role {
        OWNER, CUSTOMER, TEMPUSER
    }

}