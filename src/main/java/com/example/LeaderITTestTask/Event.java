package com.example.LeaderITTestTask;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    private String type;

    public Event() {

    }

    public Event(Device device, String type) {
        this.device = device;
        this.type = type;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getType() {
        return type;
    }
}