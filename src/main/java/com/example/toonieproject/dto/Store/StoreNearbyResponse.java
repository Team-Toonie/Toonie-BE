package com.example.toonieproject.dto.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StoreNearbyResponse {

    private Long id;
    private String storeName;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;

}
