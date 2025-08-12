package com.example.toonieproject.dto.Book;

import com.example.toonieproject.entity.Book.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data // getter, setter, toString, equals, hashCode 자동 생성.
public class SingleBookDetailResponse {

    private Long bookId;

    private Long seriesId;

    private Long storeId;

    private Integer rentalPrice;

    private Integer seriesNum;

    private Boolean isRentable;

    private Integer ageLimit;

    public SingleBookDetailResponse(Book book) {
        this.bookId = book.getId();
        this.seriesId = book.getSeries().getSeriesId();
        this.storeId = book.getStore().getId();
        this.rentalPrice = book.getRentalPrice();
        this.seriesNum = book.getSeriesNum();
        this.isRentable = book.getIsRentable();
        this.ageLimit = book.getAgeLimit();
    }
}



