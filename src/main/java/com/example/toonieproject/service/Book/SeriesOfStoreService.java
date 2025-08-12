package com.example.toonieproject.service.Book;

import com.example.toonieproject.dto.Series.AddSeriesOfStoreRequest;
import com.example.toonieproject.dto.Series.SeriesOfStoreDetailResponse;
import com.example.toonieproject.entity.Book.Series;
import com.example.toonieproject.entity.Book.SeriesOfStore;
import com.example.toonieproject.entity.Book.SeriesOfStoreId;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Book.BookRepository;
import com.example.toonieproject.repository.Book.SeriesOfStoreRepository;
import com.example.toonieproject.repository.Book.SeriesRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import com.example.toonieproject.util.auth.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@Service
public class SeriesOfStoreService {

    private final SeriesRepository seriesRepository;
    private final StoreRepository storeRepository;
    private final SeriesOfStoreRepository seriesOfStoreRepository;
    private final BookRepository bookRepository;



    public void add(AddSeriesOfStoreRequest request) throws AccessDeniedException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        // storeId로 Store 가져오기
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        // ownerId 검증
        if (!store.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }


        // 1. 시리즈와 가게 엔티티 조회
        Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + request.getSeriesId()));
        store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Series not found with id: " + request.getStoreId()));

        // 2. 복합 키 생성
        SeriesOfStoreId seriesOfStoreId = new SeriesOfStoreId(request.getSeriesId(), request.getStoreId());
        SeriesOfStore seriesOfStore = new SeriesOfStore();

        // 엔티티 생성 및 저장
        seriesOfStore.setId(seriesOfStoreId); // 복합 키 설정
        seriesOfStore.setSeries(series);
        seriesOfStore.setStore(store);
        seriesOfStore.setMaxOfRentalPeriod(request.getMaxOfRentalPeriod());
        seriesOfStore.setVolume(0);

        seriesOfStoreRepository.save(seriesOfStore);


    }

    public SeriesOfStoreDetailResponse getSeriesOfStoreDetails(long storeId, long seriesId){


        SeriesOfStoreId compositeId = new SeriesOfStoreId(seriesId, storeId);
        SeriesOfStore result = seriesOfStoreRepository.findById(compositeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "SeriesOfStore not found with storeId " + storeId + " and seriesId " + seriesId
                ));

        return SeriesOfStoreDetailResponse.builder()
                .seriesId(result.getId().getSeriesId())
                .storeId(result.getId().getStoreId())
                .maxOfRentalPeriod(result.getMaxOfRentalPeriod())
                .volume(result.getVolume())
                .build();
    }


    @Transactional
    public void updateVolumeByBook(Long storeId, Long seriesId) {
        int bookCount = bookRepository.countByStoreIdAndSeries_SeriesId(storeId, seriesId);

        SeriesOfStoreId id = new SeriesOfStoreId(seriesId, storeId);
        SeriesOfStore seriesOfStore = seriesOfStoreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SeriesOfStore 관계가 존재하지 않습니다."));

        seriesOfStore.setVolume(bookCount);
        seriesOfStoreRepository.save(seriesOfStore);
    }


}
