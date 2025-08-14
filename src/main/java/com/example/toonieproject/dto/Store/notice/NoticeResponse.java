package com.example.toonieproject.dto.Store.notice;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeResponse {

    Long noticeId;
    String title;
    String content;
    Long storeId;
    LocalDateTime createdAt;
}
