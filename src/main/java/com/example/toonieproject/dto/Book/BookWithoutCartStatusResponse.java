package com.example.toonieproject.dto.Book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookWithoutCartStatusResponse {
    private Long bookId;
    private Integer seriesNum;
    private Integer rentalPrice;
    private Boolean isRentable;
    private Integer ageLimit;

    //private Boolean inCart; // 장바구니 포함 여부
}
