package com.example.LeaderITTestTask;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final DeviceLastActivityRepository deviceLastActivityRepository;

    public DeviceService(DeviceRepository deviceRepository, DeviceLastActivityRepository deviceLastActivityRepository) {
        this.deviceRepository = deviceRepository;
        this.deviceLastActivityRepository = deviceLastActivityRepository;
    }

    public Optional<Device> getDevice(Long serialNumber) {
        return deviceRepository.findBySerial(serialNumber);
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public Optional<Device> getById(Long deviceId) {
        return deviceRepository.findById(deviceId);
    }

    public Optional<Device> getBySerial(Long serial) {
        return deviceRepository.findBySerial(serial);
    }

    public List<Device> findAllActiveDevices() {
        return deviceRepository.findActiveDevices();
    }

    public void setDeviceActive(Device device, Event event) {
        if (deviceLastActivityRepository.existsByDevice_Id(device.getId())) {
            deviceLastActivityRepository.updateLastActivity(event, device);
            return;
        }
        DeviceLastActivity deviceLastActivity = new DeviceLastActivity(device, event);
        deviceLastActivityRepository.save(deviceLastActivity);
    }
}
