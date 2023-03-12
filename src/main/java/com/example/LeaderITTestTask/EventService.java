package com.example.LeaderITTestTask;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public List<Event> findAll(Long deviceSerial, SelectionData selectionData) {
        int page = selectionData.getPage();
        PageRequest pageRequest = PageRequest.of(page, 50, Sort.by("creationDate"));
        return eventRepository.findEvents(
                selectionData.getStartDateTime(),
                selectionData.getEndDateTime(),
                selectionData.getType(),
                deviceSerial,
                pageRequest
        );
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }
}
