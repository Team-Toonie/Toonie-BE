package com.example.toonieproject.exception;

import com.example.toonieproject.dto.Error.ErrorCode;
import com.example.toonieproject.dto.Error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 Bad Request - 사용자 잘못된 요청
     */

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        ErrorCode.BAD_REQUEST
                ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "요청한 리소스를 찾을 수 없습니다.",
                        ErrorCode.NOT_FOUND
                ));
    }

    /**
     * 401/403 인증 또는 인가 실패
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ErrorCode code = switch (status) {
            case UNAUTHORIZED -> ErrorCode.INVALID_TOKEN;
            case FORBIDDEN -> ErrorCode.ACCESS_DENIED;
            default -> null;
        };

        if (code != null) {
            return ResponseEntity.status(status)
                    .body(new ErrorResponse(status.value(), ex.getReason(), code));
        }

        // 그 외 ResponseStatusException은 서버 오류로 처리
        return handleServerError(ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "접근 권한이 없습니다.",
                        ErrorCode.ACCESS_DENIED
                ));
    }

    /**
     * 404 Not Found - 리소스 없음
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        ex.printStackTrace();

        String message = ex.getMessage();
        ErrorCode code;

        if (message.contains("User")) {
            code = ErrorCode.USER_NOT_FOUND;
        } else if (message.contains("Store")) {
            code = ErrorCode.STORE_NOT_FOUND;
        } else if (message.contains("Series")) {
            code = ErrorCode.SERIES_NOT_FOUND;
        } else if (message.contains("Refresh")) {
            code = ErrorCode.REFRESH_TOKEN_NOT_FOUND;
        } else {
            code = ErrorCode.ENTITY_NOT_FOUND;
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        message,
                        code
                ));
    }

    /**
     * 500 Internal Server Error - 시스템 예외
     */

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataAccessException ex) {
        ex.printStackTrace(); // 서버 로그 출력
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "데이터베이스 오류가 발생했습니다.",
                        ErrorCode.DATABASE_ERROR
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception ex) {
        ex.printStackTrace(); // 서버 로그 출력
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "서버 내부 오류가 발생했습니다.",
                        ErrorCode.INTERNAL_SERVER_ERROR
                ));
    }
}

