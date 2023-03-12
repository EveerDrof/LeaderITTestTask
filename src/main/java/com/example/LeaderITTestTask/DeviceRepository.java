package com.example.LeaderITTestTask;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findBySerial(Long serial);

    @Query(
            nativeQuery = true,
            value = "SELECT (d.*) FROM device_last_activity dla\n" +
                    "JOIN device d  ON d.id  = dla.device_id"
    )
    List<Device> findActiveDevices();

    @Query(
            "SELECT d " +
                    " FROM Device d" +
                    " WHERE " +
                    " (" +
                    " (cast(:startDate as TIMESTAMP) IS NULL OR d.creationDate >= :startDate) AND " +
                    " (cast(:endDate as TIMESTAMP) IS NULL OR d.creationDate <= :endDate) AND " +
                    " (:type IS NULL OR d.type = :type) " +
                    " )"
    )
    List<Device> findDevices(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") String type,
            Pageable pageable
    );
}
