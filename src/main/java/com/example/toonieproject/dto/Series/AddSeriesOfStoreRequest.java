package com.example.toonieproject.dto.Series;

import jakarta.validation.constraints.Min;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data // getter, setter, toString, equals, hashCode 자동 생성.
public class AddSeriesOfStoreRequest {

    private Long seriesId;
    private Long storeId;
    @Min(value = 1)
    private Integer maxOfRentalPeriod;
    // @Min(value = 0)
    // private Integer volume; // 보유 중인 권 수

}
