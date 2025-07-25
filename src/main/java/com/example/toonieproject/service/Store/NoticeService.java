package com.example.toonieproject.service.Store;

import com.example.toonieproject.dto.Store.notice.AddNoticeRequest;
import com.example.toonieproject.dto.Store.notice.NoticeResponse;
import com.example.toonieproject.entity.Store.Notice;
import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Store.NoticeRepository;
import com.example.toonieproject.repository.Store.StoreRepository;
import com.example.toonieproject.util.auth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final StoreRepository storeRepository;

    public NoticeResponse add(AddNoticeRequest request) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 1. storeId로 Store 엔티티 조회
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new NoSuchElementException("Store not found"));

        // 2. 현재 로그인한 사용자와 Store의 ownerId 비교
        if (!store.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }

        // 3. Notice 저장
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setStoreId(store.getId());  // 단순히 storeId 저장
        notice.setCreatedAt(LocalDateTime.now());

        noticeRepository.save(notice);

        return new NoticeResponse(notice.getId(), notice.getTitle(), notice.getContent(),
                notice.getStoreId(), notice.getCreatedAt());
    }


    public void delete(Long noticeId) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 1. 공지 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoSuchElementException("공지사항을 찾을 수 없습니다."));

        // 2. storeId로 store 조회
        Store store = storeRepository.findById(notice.getStoreId())
                .orElseThrow(() -> new NoSuchElementException("해당 매장을 찾을 수 없습니다."));

        // 3. 현재 사용자와 매장 소유자 비교
        if (!store.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission.");
        }

        // 4. 삭제
        noticeRepository.delete(notice);
    }

    public NoticeResponse getNotice(Long noticeId) {
        return toResponse(getEntity(noticeId));
    }

    public Page<NoticeResponse> getNoticesByStoreId(Long storeId, Pageable pageable) {
        return noticeRepository.findByStoreId(storeId, pageable)
                .map(this::toResponse);
    }

    private Notice getEntity(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("공지사항을 찾을 수 없습니다."));
    }

    private NoticeResponse toResponse(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getStoreId(),
                notice.getCreatedAt());
    }
}
