package com.example.LeaderITTestTask;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;

@RestController
public class Controller {
    private final DeviceService deviceService;
    private final Security security;

    public Controller(DeviceService devicesAndEventsService, Security security) {
        this.deviceService = devicesAndEventsService;
        this.security = security;
    }

    private <T> ResponseEntity<ApiResponse<T>> wrapResponse(
            T payload,
            String message,
            HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse<>(payload, message), httpStatus);
    }

    private <T> ResponseEntity<ApiResponse<T>> wrapOk(T payload) {
        return wrapResponse(payload, "Success", HttpStatus.OK);
    }

    private ResponseEntity<ApiResponse<Object>> wrapClientError(
            String message,
            HttpStatus httpStatus
    ) {
        return wrapResponse(null, message, httpStatus);
    }

    private ResponseEntity<ApiResponse<Object>> wrapBadRequest(String message) {
        return wrapClientError(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiResponse<Object>> wrapNotFound(String message) {
        return wrapClientError(message, HttpStatus.NOT_FOUND);
    }

    @PostMapping(
            value = "/device",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Object>> addNewDevice(@RequestBody Device device) {
        try {
            device = deviceService.save(device);
        } catch (DataIntegrityViolationException exception) {
            return wrapBadRequest("This serial already exists");
        }
        String secret;
        try {
            secret = security.nextKey();
            device.setSecretHash(security.hash(secret));
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            return wrapResponse(null, "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("secretKey", secret);
        return wrapOk(result);
    }

    @GetMapping(value = "/device/{serialNumber}")
    public ResponseEntity<ApiResponse<Object>> getDeviceBySerialNumber(@PathVariable Long serialNumber) {
        Optional<Device> result = deviceService.getDevice(serialNumber);
        if (result.isEmpty()) {
            return wrapNotFound("No device with this serial");
        }
        return wrapOk(result.get());
    }
}
