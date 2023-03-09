package com.example.LeaderITTestTask;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.LeaderITTestTask.Utils.wrapOk;

@RestController
public class EventController {
    @PostMapping("/event")
    public ResponseEntity<ApiResponse<Object>> addNewEvent() {
        return wrapOk(null);
    }
}
