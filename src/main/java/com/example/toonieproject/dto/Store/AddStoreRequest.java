package com.example.toonieproject.dto.Store;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddStoreRequest {

    @NotBlank
    @Size(max = 100)
    private String name; // 가게 이름

    @NotBlank
    @Size(max = 15)
    private String phoneNumber; // 전화번호

    @NotNull
    private Long ownerId; // 소유자 ID

    @Size(max = 255)
    private String representUrl; // 대표 URL (선택 사항)

    @NotNull
    private Boolean isOpen; // 영업 상태

    @Size(max = 500)
    private String info; // 추가 정보 (선택 사항)

    @NotBlank
    @Size(max = 255)
    private String address; // 기본 주소

    @Size(max = 255)
    private String detailedAddress; // 상세 주소 (선택 사항)

    @NotNull
    @Digits(integer = 3, fraction = 7)
    private BigDecimal lat; // 위도

    @NotNull
    @Digits(integer = 3, fraction = 7)
    private BigDecimal lng; // 경도

    @Size(max = 255)
    private String image; // 이미지 주소


}
