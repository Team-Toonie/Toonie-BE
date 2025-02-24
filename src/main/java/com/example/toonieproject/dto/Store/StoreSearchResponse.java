package com.example.toonieproject.dto.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StoreSearchResponse {

    private Long storeId;
    private String storeName;
    private String location;
}
