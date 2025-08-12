package com.example.toonieproject.dto.Store;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreMapResponse {

    private Long storeId;
    private BigDecimal lat;
    private BigDecimal lng;

    private String name;
    private String address;
    private String detailed_address;
    private String image;
}
