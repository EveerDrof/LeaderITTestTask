package com.example.LeaderITTestTask;

import com.google.gson.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class Utils {

    public static Gson createGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(
                            JsonElement json,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext
                    ) throws JsonParseException {
                        return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
                    }
                }).create();
    }

    public static class Response {
        public static <T> ResponseEntity<ApiResponse<T>> response(
                T payload,
                String message,
                HttpStatus httpStatus) {
            return new ResponseEntity<>(new ApiResponse<>(payload, message), httpStatus);
        }

        public static <T> ResponseEntity<ApiResponse<T>> ok(T payload) {
            return response(payload, "Success", HttpStatus.OK);
        }

        public static ResponseEntity<ApiResponse<Object>> clientError(
                String message,
                HttpStatus httpStatus
        ) {
            return response(null, message, httpStatus);
        }

        public static ResponseEntity<ApiResponse<Object>> badRequest(String message) {
            return clientError(message, HttpStatus.BAD_REQUEST);
        }

        public static ResponseEntity<ApiResponse<Object>> notFound(String message) {
            return clientError(message, HttpStatus.NOT_FOUND);
        }

        public static ResponseEntity<ApiResponse<Object>> internalError() {
            return response(null, "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        public static ResponseEntity<ApiResponse<Object>> unauthorized() {
            return clientError("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        public static ResponseEntity<ApiResponse<Object>> unauthorized(String message) {
            return clientError("Incorrect key", HttpStatus.UNAUTHORIZED);
        }

        public static ResponseEntity<ApiResponse<Object>> ok() {
            return ok("Success");
        }
    }
}
