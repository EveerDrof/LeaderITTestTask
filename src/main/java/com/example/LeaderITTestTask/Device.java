package com.example.LeaderITTestTask;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String secretHash;

    public Device() {

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


    public String getSecretHash() {
        return secretHash;
    }

    public void setSecretHash(String hash) {
        this.secretHash = hash;
    }
}
