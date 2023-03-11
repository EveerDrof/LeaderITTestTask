package com.example.LeaderITTestTask;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreationDateBetweenAndDevice_Serial(
            LocalDateTime creationDateStart,
            LocalDateTime creationDateEnd,
            Long serial
    );

    List<Event> findByCreationDateBetweenAndTypeAndDevice_Serial(
            LocalDateTime creationDateStart,
            LocalDateTime creationDateEnd,
            String type,
            Long serial
    );


}
