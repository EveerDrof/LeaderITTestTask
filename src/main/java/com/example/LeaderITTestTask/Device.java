package com.example.LeaderITTestTask;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "serial", nullable = false, unique = true)
    private Long serial;
    private String name;
    private String deviceType;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;
    private String secretHash;

    public Device() {

    }

    public Device(Long serial, String name, String deviceType) {
        this.serial = serial;
        this.name = name;
        this.deviceType = deviceType;
    }

    public Long getId() {
        return id;
    }

    public Long getSerial() {
        return serial;
    }

    public String getName() {
        return name;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }


    public void setSecretHash(String hash) {
        this.secretHash = hash;
    }
}
