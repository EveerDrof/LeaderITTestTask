package com.example.LeaderITTestTask;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<Device> getDevice(Long serialNumber) {
        return deviceRepository.findBySerial(serialNumber);
    }

    public void save(Device device) {
        deviceRepository.save(device);
    }
}