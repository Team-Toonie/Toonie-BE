package com.example.toonieproject.dto.Rental.Cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CartBookDto {
    private Long bookId;
    private String seriesName;
    private String seriesImageUrl;
    private Integer rentalPrice;
    private Integer seriesNum;
    private Integer ageLimit;
}
