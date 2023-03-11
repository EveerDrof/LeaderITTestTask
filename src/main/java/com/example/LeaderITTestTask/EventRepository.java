package com.example.LeaderITTestTask;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreationDateBetweenAndDevice_Serial(
            LocalDateTime creationDateStart,
            LocalDateTime creationDateEnd,
            Long serial,
            Pageable pageable
    );

    List<Event> findByCreationDateBetweenAndTypeAndDevice_Serial(
            LocalDateTime creationDateStart,
            LocalDateTime creationDateEnd,
            String type,
            Long serial,
            Pageable pageable
    );


}
