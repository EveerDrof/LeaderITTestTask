package com.example.LeaderITTestTask;

import jakarta.persistence.*;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    @Column(name = "serial", nullable = false, unique = true)
    private Long serial;
    private String deviceType;

    public Device() {

    }

    public Device(String name, Long serial, String deviceType) {
        this.name = name;
        this.serial = serial;
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
}
