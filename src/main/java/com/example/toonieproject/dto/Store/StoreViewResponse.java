package com.example.toonieproject.dto.Store;

import com.example.toonieproject.entity.Store.AddressOfStore;
import com.example.toonieproject.entity.Store.Store;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreViewResponse {

    private Long storeId;
    private String storeName;
    private String phoneNumber;
    private String representUrl;
    private Boolean isOpen;
    private String info;
    private String image;

    private String address; // 기본 주소
    private String detailedAddress; // 상세 주소
    private BigDecimal lat; // 위도
    private BigDecimal lng; // 경도

}
