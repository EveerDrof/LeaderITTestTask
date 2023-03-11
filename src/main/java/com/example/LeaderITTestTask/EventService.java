package com.example.LeaderITTestTask;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final DeviceRepository deviceRepository;

    public EventService(
            EventRepository eventRepository,
            DeviceRepository deviceRepository
    ) {
        this.eventRepository = eventRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<Event> findAll(Long deviceSerial, HashMap<String, Object> parameters) {
        LocalDateTime earliestDate = (LocalDateTime) parameters.get("earliestDate");
        LocalDateTime latestDate = (LocalDateTime) parameters.get("latestDate");
        String type;
        if (!parameters.containsKey("type")) {
            return eventRepository.findByCreationDateBetweenAndDevice_Serial(earliestDate, latestDate, deviceSerial);
        }
        type = (String) parameters.get("type");
        return eventRepository.findByCreationDateBetweenAndTypeAndDevice_Serial(
                earliestDate,
                latestDate,
                type,
                deviceSerial
        );
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }
}
