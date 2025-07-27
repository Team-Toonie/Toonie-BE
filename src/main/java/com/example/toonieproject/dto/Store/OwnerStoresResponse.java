package com.example.toonieproject.dto.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerStoresResponse {

    private Long storeId;
    private String storeName;
    private String address;
    private String iamge;

}