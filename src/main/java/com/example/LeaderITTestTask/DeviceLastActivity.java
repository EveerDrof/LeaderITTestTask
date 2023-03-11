package com.example.LeaderITTestTask;

import jakarta.persistence.*;

@Entity
@Table(name = "device_last_activity")
public class DeviceLastActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    public DeviceLastActivity(Device device, Event event) {
        this.device = device;
        this.event = event;
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

}