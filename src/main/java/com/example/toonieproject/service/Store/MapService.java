package com.example.toonieproject.service.Store;


import com.example.toonieproject.dto.Store.StoreFindBySeriesResponse;
import com.example.toonieproject.dto.Store.StoreMapResponse;
import com.example.toonieproject.dto.Store.StoreNearbyResponse;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Book.SeriesRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class MapService {
    private final StoreRepository storeRepository;
    private final SeriesRepository seriesRepository;

    public List<StoreNearbyResponse> findNearbyStores(BigDecimal lat, BigDecimal lng, int page) {

        int pageSize = 10;
        int offset = (page - 1) * pageSize;

        List<Store> stores = storeRepository.findNearbyStores(lat, lng, pageSize, offset);

        return stores.stream()
                .map(store -> new StoreNearbyResponse(
                        store.getId(),
                        store.getName(),
                        store.getAddressInfo().getAddress(),
                        store.getAddressInfo().getLat(),
                        store.getAddressInfo().getLng()
                ))
                .collect(Collectors.toList());

    }


    public List<StoreMapResponse> findStoresInBounds(BigDecimal minLat, BigDecimal maxLat, BigDecimal minLng, BigDecimal maxLng) {
        List<Store> stores = storeRepository.findStoresInBounds(minLat, maxLat, minLng, maxLng);
        return stores.stream()
                .map(store -> new StoreMapResponse(
                        store.getId(),
                        store.getAddressInfo().getLat(),
                        store.getAddressInfo().getLng(),
                        store.getName(),
                        store.getAddressInfo().getAddress(),
                        store.getAddressInfo().getDetailedAddress(),
                        store.getImage()
                ))
                .collect(Collectors.toList());
    }


    public List<StoreFindBySeriesResponse> getStoresBySeriesId(Long seriesId, double lat, double lon) {

        // 1. 시리즈 존재 여부 확인
        if (!seriesRepository.existsById(seriesId)) {
            throw new EntityNotFoundException("Series with ID " + seriesId + " not found.");
        }

        // 2. 가게 목록 조회 (쿼리 결과가 Object[] 배열 형태로 반환됨)
        List<Object[]> results = storeRepository.findStoresBySeriesIdWithDistance(seriesId, lat, lon);

        // 3. DTO로 변환
        return results.stream().map(result -> new StoreFindBySeriesResponse(
                ((Number) result[0]).longValue(),  // store_id
                (String) result[1],               // store_name
                (String) result[2],               // address
                (String) result[3],
                (BigDecimal) result[4],           // lat
                (BigDecimal) result[5],           // lng
                ((Number) result[6]).doubleValue() // distance
        )).toList();

    }
}
