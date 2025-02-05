package com.example.toonieproject.service.Book;

import com.example.toonieproject.dto.Series.AddSeriesOfStoreRequest;
import com.example.toonieproject.entity.Book.Series;
import com.example.toonieproject.entity.Book.SeriesOfStore;
import com.example.toonieproject.entity.Book.SeriesOfStoreId;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Book.SeriesOfStoreRepository;
import com.example.toonieproject.repository.Book.SeriesRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SeriesOfStoreService {

    private final SeriesRepository seriesRepository;
    private final StoreRepository storeRepository;
    private final SeriesOfStoreRepository seriesOfStoreRepository;



    public void add(AddSeriesOfStoreRequest request, long storeId, long seriesId) {

        // 1. 시리즈와 가게 엔티티 조회
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + seriesId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + storeId));

        // 2. 복합 키 생성
        SeriesOfStoreId seriesOfStoreId = new SeriesOfStoreId(seriesId, storeId);
        SeriesOfStore seriesOfStore = new SeriesOfStore();

        // 엔티티 생성 및 저장
        seriesOfStore.setId(seriesOfStoreId); // 복합 키 설정
        seriesOfStore.setSeries(series);
        seriesOfStore.setStore(store);
        seriesOfStore.setMaxOfRentalPeriod(request.getMaxOfRentalPeriod());
        seriesOfStore.setVolume(request.getVolume());

        seriesOfStoreRepository.save(seriesOfStore);


    }



}
