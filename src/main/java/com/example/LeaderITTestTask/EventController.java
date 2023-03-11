package com.example.LeaderITTestTask;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.LeaderITTestTask.Utils.Response.*;

@RestController
public class EventController {
    private final DeviceService deviceService;
    private final PasswordEncoder passwordEncoder;
    private final EventService eventService;

    public EventController(
            DeviceService deviceService,
            PasswordEncoder passwordEncoder,
            EventService eventService) {
        this.deviceService = deviceService;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
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
            @RequestBody HashMap<String, Object> parameters
    ) {
        if (parameters.containsKey("latestDate")) {
            parameters.put("latestDate", LocalDateTime.parse((String) parameters.get("latestDate")));
        }
        if (parameters.containsKey("earliestDate")) {
            parameters.put("earliestDate", LocalDateTime.parse((String) parameters.get("earliestDate")));
        }
        List<Event> events = eventService.findAll(deviceSerial, parameters);
        return ok(events);
    }
}
