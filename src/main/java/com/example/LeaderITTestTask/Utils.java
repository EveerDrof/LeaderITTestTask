package com.example.LeaderITTestTask;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utils {
    public static <T> ResponseEntity<ApiResponse<T>> wrapResponse(
            T payload,
            String message,
            HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse<>(payload, message), httpStatus);
    }

    public static <T> ResponseEntity<ApiResponse<T>> wrapOk(T payload) {
        return wrapResponse(payload, "Success", HttpStatus.OK);
    }

    public static ResponseEntity<ApiResponse<Object>> wrapClientError(
            String message,
            HttpStatus httpStatus
    ) {
        return wrapResponse(null, message, httpStatus);
    }

    public static ResponseEntity<ApiResponse<Object>> wrapBadRequest(String message) {
        return wrapClientError(message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ApiResponse<Object>> wrapNotFound(String message) {
        return wrapClientError(message, HttpStatus.NOT_FOUND);
    }
}
