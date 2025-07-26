package com.example.toonieproject.dto.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status; // http 상태 코드
    private String message; // 에러 메시지
    private ErrorCode code; // 개발자용 에러 코드
}

