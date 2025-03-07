package com.example.toonieproject.dto.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreFindBySeriesResponse {

    private Long storeId;
    private String storeName;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private double distance;


}
