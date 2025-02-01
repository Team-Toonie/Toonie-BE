package com.example.toonieproject.entity.Book;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SeriesOfStoreId implements Serializable {

    private Long seriesId; // 시리즈 ID
    private Long storeId; // 가게 ID
}
