package com.example.toonieproject.dto.Store.notice;


import com.example.toonieproject.entity.Store.Store;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddNoticeRequest {

    private String title;
    private String content;
    private Long storeId;
}
