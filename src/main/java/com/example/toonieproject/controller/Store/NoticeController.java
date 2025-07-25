package com.example.toonieproject.controller.Store;


import com.example.toonieproject.dto.Store.notice.AddNoticeRequest;
import com.example.toonieproject.dto.Store.notice.NoticeResponse;
import com.example.toonieproject.entity.Store.Notice;
import com.example.toonieproject.service.Store.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<NoticeResponse> create(@RequestBody AddNoticeRequest request) throws AccessDeniedException {

        NoticeResponse response = noticeService.add(request);


        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@PathVariable Long noticeId) throws AccessDeniedException {

        noticeService.delete(noticeId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> get(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<NoticeResponse>> getByStore(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(noticeService.getNoticesByStoreId(storeId, pageable));
    }



}
