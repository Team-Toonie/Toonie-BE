package com.example.toonieproject.dto.Rental.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartRequest {

    private long userId;
    private long storeId;
    private long[] bookId;

}
