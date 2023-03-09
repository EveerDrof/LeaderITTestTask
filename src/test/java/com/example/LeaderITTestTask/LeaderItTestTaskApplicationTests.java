package com.example.LeaderITTestTask;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class LeaderItTestTaskApplicationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeAll
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    private ResultActions postDevice(String name, long serial, String deviceType) throws Exception {
        return mockMvc.perform(
                post("/device")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                 "  \"name\": \"" + name + "\",\n" +
                                 "  \"serial\": " + serial + ",\n" +
                                 "  \"deviceType\": \"" + deviceType + "\"\n" +
                                 "}")
        );
    }

    @Test
    public void addNewDevice_should_return_200() throws Exception {
        postDevice("aaa", 0, "asd")
                .andExpect(status().isOk()).andExpect(
                        jsonPath("$.message")
                                .value("Success")
                ).andExpect(jsonPath("$.payload.secretKey").exists());
    }

    @Test
    public void getCreatedDevice_should_return_device() throws Exception {
        postDevice("My Refrigerator", 12312, "Refrigerator");
        mockMvc.perform(get("/device/12312"))
                .andExpect(status().isOk()).andExpect(
                        jsonPath("$.payload.name")
                                .value("My Refrigerator")
                ).andExpect(jsonPath("$.payload.deviceType")
                        .value("Refrigerator"))
                .andExpect(jsonPath("$.payload.creationDate").exists());
    }

    @Test
    public void createDevicesWithSameSerial_should_return_400() throws Exception {
        postDevice("My Refrigerator", 12312, "Refrigerator");
        postDevice("My Refrigerator", 12312, "Refrigerator")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("This serial already exists")
                );
    }

    @Test
    public void nonExistentSerialDeviceGetCheck_should_return_404() throws Exception {
        mockMvc.perform(get("/device/291"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No device with this serial")
                );
    }
}
