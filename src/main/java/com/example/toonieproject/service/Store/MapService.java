package com.example.toonieproject.service.Store;


import com.example.toonieproject.dto.Store.StoreMapResponse;
import com.example.toonieproject.dto.Store.StoreNearbyResponse;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Store.StoreRepository;
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

    public List<StoreNearbyResponse> findNearbyStores(BigDecimal lat, BigDecimal lng, int page) {

        int pageSize = 10;
        int offset = (page - 1) * pageSize;

        List<Store> stores = storeRepository.findNearbyStores(lat, lng, pageSize, offset);

        return stores.stream()
                .map(store -> new StoreNearbyResponse(
                        store.getId(),
                        store.getName(),
                        store.getAddress().getAddress(),
                        store.getAddress().getLat(),
                        store.getAddress().getLng()
                ))
                .collect(Collectors.toList());

    }


    public List<StoreMapResponse> findStoresInBounds(BigDecimal minLat, BigDecimal maxLat, BigDecimal minLng, BigDecimal maxLng) {
        List<Store> stores = storeRepository.findStoresInBounds(minLat, maxLat, minLng, maxLng);
        return stores.stream()
                .map(store -> new StoreMapResponse(store.getId(), store.getAddress().getLat(), store.getAddress().getLng()))
                .collect(Collectors.toList());
    }


}
