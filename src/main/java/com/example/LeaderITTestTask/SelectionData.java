package com.example.LeaderITTestTask;

import java.time.LocalDateTime;

public class SelectionData {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String type;
    private Integer page;

    public SelectionData(LocalDateTime startDateTime, LocalDateTime endDateTime, String type, Integer page) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
        this.page = page;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getType() {
        return type;
    }

    public Integer getPage() {
        return page;
    }
}
