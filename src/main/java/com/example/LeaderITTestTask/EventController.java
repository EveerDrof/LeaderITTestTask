package com.example.LeaderITTestTask;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Optional;

import static com.example.LeaderITTestTask.Utils.Response.*;

@RestController
public class EventController {
    private final DeviceService deviceService;
    private final PasswordEncoder passwordEncoder;

    public EventController(DeviceService deviceService, PasswordEncoder passwordEncoder) {
        this.deviceService = deviceService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(
            value = "/event",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Object>> addNewEvent(
            @RequestBody HashMap<String, Object> map
    ) {
        Long deviceId = Long.valueOf((Integer) map.get("deviceId"));
        Optional<Device> deviceOptional = deviceService.getById(deviceId);
        if (!deviceOptional.isPresent()) {
            return badRequest("No such device");
        }
        Device device = deviceOptional.get();
        String secretKey = (String) map.get("secretKey");
        if (!passwordEncoder.matches(secretKey, device.getSecretHash())) {
            return unauthorized("Incorrect key");
        }
        return ok();
    }
}
