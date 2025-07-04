package com.example.toonieproject.dto.Series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data // getter, setter, toString, equals, hashCode 자동 생성.
@Builder
public class SeriesOfStoreDetailResponse {

    private Long seriesId;
    private Long storeId;
    private Integer maxOfRentalPeriod;
    private Integer volume; // 보유 중인 권 수

}
