package com.example.toonieproject.dto.Book;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data // getter, setter, toString, equals, hashCode 자동 생성.
public class AddBookRequest {

    private Long seriesId;

    private Long storeId;

    private Integer rentalPrice;

    private Integer seriesNum;

    private Boolean isRentable;

    @Min(value = 0, message = "연령 제한은 0 이상이어야 합니다.")
    private Integer ageLimit;
}

