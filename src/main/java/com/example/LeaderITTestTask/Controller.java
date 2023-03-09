package com.example.LeaderITTestTask;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class Controller {
    private DeviceService deviceService;

    public Controller(DeviceService devicesAndEventsService) {
        this.deviceService = devicesAndEventsService;
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

    private ResponseEntity<ApiResponse<Object>> wrapBadRequest(String message) {
        return wrapResponse(null, message, HttpStatus.BAD_REQUEST);
    }

    @PostMapping(
            value = "/device",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Object>> addNewDevice(@RequestBody Device device) {
        try {
            deviceService.save(device);
        } catch (DataIntegrityViolationException exception) {
            return wrapBadRequest("This serial already exists");
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("secretKey", "12345");
        return wrapOk(result);
    }

    @GetMapping(value = "/device/{serialNumber}")
    public ResponseEntity<ApiResponse<Device>> getDeviceBySerialNumber(@PathVariable Long serialNumber) {
        Device device = deviceService.getDevice(serialNumber).get();
        return wrapOk(device);
    }
}
