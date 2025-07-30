package com.example.toonieproject.dto.Rental.Cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long storeId;
    private String storeName;
    private List<CartBookDto> books;
}
