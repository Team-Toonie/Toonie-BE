package com.example.toonieproject.service.Store;


import com.example.toonieproject.dto.Store.*;
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


    public Store add(AddStoreRequest request, MultipartFile imageFile) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // ownerId 검증
        if (!request.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }

        // 1. 가게 정보 저장
        Store store = new Store();

        store.setName(request.getName());
        User user = userRepository.findById(currentUserId) // 현재 로그인 한 사용자의 id로 생성
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + currentUserId));
        store.setUser(user);
        store.setRepresentUrl(request.getRepresentUrl());
        store.setPhoneNumber(request.getPhoneNumber());
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

        // Trie 추가
        storeTrie.insertStore(store);

        return store;
    }

    @Transactional
    public void update(Long storeId, updateStoreRequest request, MultipartFile imageFile) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 1. 기존 가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));

        // 2. 소유자 일치 여부 확인
        if (!store.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not the owner of this store.");
        }

        // 3. 값 수정
        store.setName(request.getName());
        store.setRepresentUrl(request.getRepresentUrl());
        store.setPhoneNumber(request.getPhoneNumber());
        store.setInfo(request.getInfo());

        // 4. 이미지 변경

        // 기존 이미지 삭제
        String oldImageUrl = store.getImage();
        String oldFileName = firebaseStorageService.extractFilenameFromUrl(oldImageUrl);
        firebaseStorageService.deleteImage(oldFileName);


        if (imageFile != null && !imageFile.isEmpty()) {

            String imageUrl;
            try {
                imageUrl = firebaseStorageService.uploadImage(imageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            store.setImage(imageUrl);
        }

        storeRepository.save(store);

        // 5. 주소 업데이트
        AddressOfStore address = addressOfStoreRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found for storeId: " + storeId));

        address.setAddress(request.getAddress());
        address.setDetailedAddress(request.getDetailedAddress());
        address.setLat(request.getLat());
        address.setLng(request.getLng());

        addressOfStoreRepository.save(address);

        // 6. Trie 갱신
        storeTrie.updateStore(store); // 직접 구현한 경우에만 필요
    }

    @Transactional
    public void delete(Long storeId) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));

        // 권한 확인
        if (!store.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not the owner of this store.");
        }

        System.out.println("delete store id: " + storeId);
        // 이미지 삭제 (Firebase에서)
        if (store.getImage() != null) {
            String fileName = firebaseStorageService.extractFilenameFromUrl(store.getImage());
            firebaseStorageService.deleteImage(fileName);
        }

        // Trie 갱신
        storeTrie.deleteStore(store);

        // 삭제
        storeRepository.deleteByStoreId(store.getId());

    }


    public List<OwnerStoresResponse> findByUserId(Long userId) throws AccessDeniedException {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        System.out.println("currentUserId: " + currentUserId + "\n" + "userId: " + userId);

        // ownerId 검증
        if (!userId.equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }

        // ownerId에 해당하는 가게 리스트 조회
        List<Store> stores = storeRepository.findByUser_Id(userId);

        List<OwnerStoresResponse> responseList = new ArrayList<>();


        for (Store store : stores) {
            // storeId로 AddressOfStore 조회
            AddressOfStore addressOfStore = addressOfStoreRepository.findById(store.getId()).orElse(null);
            String address = (addressOfStore != null) ? addressOfStore.getAddress() : "주소 없음";

            // DTO 생성 후 리스트에 추가
            responseList.add(new OwnerStoresResponse(store.getId(), store.getName(), address, store.getImage()));
        }

        return responseList;
    }


    // 가게 정보 반환
    public StoreViewResponse findByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        AddressOfStore address = addressOfStoreRepository.findById(storeId).orElseThrow();

        StoreViewResponse response = StoreViewResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .phoneNumber(store.getPhoneNumber())
                .representUrl(store.getRepresentUrl())
                .info(store.getInfo())
                .image(store.getImage())
                .address(address.getAddress())
                .detailedAddress(address.getDetailedAddress())
                .lat(address.getLat())
                .lng(address.getLng())
                .build();

        return response;

    }

    // 가게 검색
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
