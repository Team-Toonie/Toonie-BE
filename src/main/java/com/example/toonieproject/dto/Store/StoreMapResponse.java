package com.example.toonieproject.dto.Store;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StoreMapResponse {

    private Long id;
    private BigDecimal lat;
    private BigDecimal lng;
}
