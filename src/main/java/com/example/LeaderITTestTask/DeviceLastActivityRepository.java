package com.example.LeaderITTestTask;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface DeviceLastActivityRepository extends JpaRepository<DeviceLastActivity, Long> {
    void deleteByCreationDateBefore(LocalDateTime creationDate);


    @Transactional
    @Modifying
    @Query("update DeviceLastActivity d set d.event = ?1 where d.device = ?2")
    void updateLastActivity(Event event, Device device);

    boolean existsByDevice_Id(Long id);


}