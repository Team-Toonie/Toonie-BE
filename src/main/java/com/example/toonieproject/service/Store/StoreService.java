package com.example.toonieproject.service.Store;


import com.example.toonieproject.dto.Store.AddStoreRequest;
import com.example.toonieproject.dto.Store.OwnerStoresResponse;
import com.example.toonieproject.dto.Store.StoreSearchResponse;
import com.example.toonieproject.dto.Store.StoreViewResponse;
import com.example.toonieproject.entity.Store.AddressOfStore;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.entity.Auth.User;
import com.example.toonieproject.repository.Store.AddressOfStoreRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import com.example.toonieproject.repository.Auth.UserRepository;
import com.example.toonieproject.service.Storage.FirebaseStorageService;
import com.example.toonieproject.util.auth.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final AddressOfStoreRepository addressOfStoreRepository;
    private final UserRepository userRepository;
    private final StoreTrie storeTrie;
    private final FirebaseStorageService firebaseStorageService;


    public Store add(AddStoreRequest request, MultipartFile imageFile) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 1. 가게 정보 저장
        Store store = new Store();

        store.setName(request.getName());
        User user = userRepository.findById(currentUserId) // 현재 로그인 한 사용자의 id로 생성
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + currentUserId));
        store.setUser(user);
        store.setRepresentUrl(request.getRepresentUrl());
        store.setPhoneNumber(request.getPhoneNumber());
        store.setIsOpen(request.getIsOpen());
        store.setInfo(request.getInfo());

        // 이미지 링크로 변환 후 저장
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = null;
            try {
                imageUrl = firebaseStorageService.uploadImage(imageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            store.setImage(imageUrl);
        }


        store = storeRepository.saveAndFlush(store); // 트랜잭션 내에서 즉시 DB 반영

        // 2. 가게 주소 정보 저장

        AddressOfStore addressOfStore = new AddressOfStore();

        addressOfStore.setStore(store);
        addressOfStore.setAddress(request.getAddress());
        addressOfStore.setDetailedAddress(request.getDetailedAddress());
        addressOfStore.setLat(request.getLat());
        addressOfStore.setLng(request.getLng());

        addressOfStoreRepository.save(addressOfStore);

        // Trie에 추가
        storeTrie.insertStore(store);


        return store;
    }

    public List<OwnerStoresResponse> findByUserId(Long userId) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        // ownerId 검증
        if (userId.equals(currentUserId)) {
            try {
                throw new AccessDeniedException("You do not have permission");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("currentUserId: " + currentUserId + "\n" + "userId: " + userId);


        // ownerId에 해당하는 가게 리스트 조회
        List<Store> stores = storeRepository.findByUser_Id(userId);

        List<OwnerStoresResponse> responseList = new ArrayList<>();


        for (Store store : stores) {
            // storeId로 AddressOfStore 조회
            AddressOfStore addressOfStore = addressOfStoreRepository.findById(store.getId()).orElse(null);
            String address = (addressOfStore != null) ? addressOfStore.getAddress() : "주소 없음";

            // DTO 생성 후 리스트에 추가
            responseList.add(new OwnerStoresResponse(store.getId(), store.getName(), address));
        }

        return responseList;
    }


    public StoreViewResponse findByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        AddressOfStore address = addressOfStoreRepository.findById(storeId).orElseThrow();

        StoreViewResponse response = StoreViewResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .phoneNumber(store.getPhoneNumber())
                .representUrl(store.getRepresentUrl())
                .isOpen(store.getIsOpen())
                .info(store.getInfo())
                .image(store.getImage())
                .address(address.getAddress())
                .detailedAddress(address.getDetailedAddress())
                .lat(address.getLat())
                .lng(address.getLng())
                .build();

        return response;

    }

    public List<StoreSearchResponse> searchStores(String query) {

        List<Store> stores = storeTrie.searchStore(query);

        return stores.stream()
                .map(store -> StoreSearchResponse.builder()
                        .storeId(store.getId())
                        .storeName(store.getName()) // 가게 이름
                        .location(store.getAddressInfo().getAddress()) // 주소
                        .build())
                .collect(Collectors.toList());
    }

}
