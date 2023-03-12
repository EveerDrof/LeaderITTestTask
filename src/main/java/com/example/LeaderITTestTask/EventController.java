package com.example.LeaderITTestTask;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.LeaderITTestTask.Utils.Response.*;

@RestController
public class EventController {
    private final DeviceService deviceService;
    private final PasswordEncoder passwordEncoder;
    private final EventService eventService;
    private final Gson gson;

    public EventController(
            DeviceService deviceService,
            PasswordEncoder passwordEncoder,
            EventService eventService) {
        this.deviceService = deviceService;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
        this.gson = Utils.createGson();
    }

    @PostMapping(
            value = "/event",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Object>> addNewEvent(
            @RequestBody HashMap<String, Object> map
    ) {
        Long serial = Long.valueOf((Integer) map.get("deviceSerial"));
        Optional<Device> deviceOptional = deviceService.getBySerial(serial);
        if (!deviceOptional.isPresent()) {
            return badRequest("No such device");
        }
        Device device = deviceOptional.get();
        String secretKey = (String) map.get("secretKey");
        if (!passwordEncoder.matches(secretKey, device.getSecretHash())) {
            return unauthorized("Incorrect key");
        }
        Event event = new Event(device, (String) map.get("type"));
        event = eventService.save(event);
        deviceService.setDeviceActive(device, event);
        return ok();
    }

    @GetMapping(
            value = "/event/device/{deviceSerial}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Object>> getEventsList(
            @PathVariable Long deviceSerial,
            @RequestBody String parameters
    ) {
        SelectionData selectionData = gson.fromJson(parameters, SelectionData.class);
        List<Event> events = eventService.findAll(deviceSerial, selectionData);
        return ok(events);
    }
}
