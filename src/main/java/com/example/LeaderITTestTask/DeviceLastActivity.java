package com.example.LeaderITTestTask;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_last_activity")
public class DeviceLastActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    @OneToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    public DeviceLastActivity() {

    }

    public DeviceLastActivity(Device device, Event event) {
        this.device = device;
        this.event = event;
        this.creationDate = event.getCreationDate();
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}