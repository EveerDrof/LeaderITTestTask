package com.example.LeaderITTestTask;

import com.google.gson.Gson;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

import static com.example.LeaderITTestTask.Security.nextKey;
import static com.example.LeaderITTestTask.Utils.Response.*;

@RestController
public class DeviceController {
    private final DeviceService deviceService;
    private final PasswordEncoder passwordEncoder;
    private final Gson gson;

    public DeviceController(
            DeviceService devicesAndEventsService,
            PasswordEncoder passwordEncoder
    ) {
        this.deviceService = devicesAndEventsService;
        this.passwordEncoder = passwordEncoder;
        this.gson = Utils.createGson();
    }


    @PostMapping(
            value = "/device",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Object>> addNewDevice(@RequestBody Device device) {
        String secret;
        secret = nextKey();
        device.setSecretHash(passwordEncoder.encode(secret));
        try {
            deviceService.save(device);
        } catch (DataIntegrityViolationException exception) {
            return badRequest("This serial already exists");
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("secretKey", secret);
        return ok(result);
    }

    @GetMapping(value = "/device/serial/{serialNumber}")
    public ResponseEntity<ApiResponse<Object>> getDeviceBySerialNumber(@PathVariable Long serialNumber) {
        Optional<Device> result = deviceService.getDevice(serialNumber);
        if (!result.isPresent()) {
            return notFound("No device with this serial");
        }
        return ok(result.get());
    }

    @GetMapping(value = "/device/active")
    public ResponseEntity<ApiResponse<Object>> getActiveDevices() {
        deviceService.removeInactiveDevices();
        return ok(deviceService.findAllActiveDevices());
    }

    @GetMapping(value = "/device/all")
    public ResponseEntity<ApiResponse<Object>> getDevices(@RequestBody String body) {
        SelectionData selectionData = gson.fromJson(body, SelectionData.class);
        return ok(deviceService.findDevices(selectionData));
    }
}
