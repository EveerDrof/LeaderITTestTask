package com.example.LeaderITTestTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
