package com.example.LeaderITTestTask;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(
            "SELECT e " +
                    " FROM Event e" +
                    " WHERE " +
                    " (" +
                    " (cast(:startDate as TIMESTAMP) IS NULL OR e.creationDate >= :startDate) AND " +
                    " (cast(:endDate as TIMESTAMP) IS NULL OR e.creationDate <= :endDate) AND " +
                    " (:type IS NULL OR e.type = :type) AND " +
                    " (:serial IS NULL OR e.device.serial = :serial)" +
                    " )"
    )
    List<Event> findEvents(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") String type,
            @Param("serial") Long serial,
            Pageable pageable
    );
}
