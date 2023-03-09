package com.example.LeaderITTestTask;

public class ApiResponse<T> {
    private final T payload;
    private final String message;
    public ApiResponse(T payload,String message){
        this.payload = payload;
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }
}
