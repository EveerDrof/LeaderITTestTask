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

    @BeforeAll
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void addNewDevice_should_return_200() throws Exception {
        mockMvc.perform(
                post("/device")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                  "name": "My Refrigerator",
                                  "serial": 12312,
                                  "deviceType": "Refrigerator"
                                }""")
        ).andExpect(status().isOk()).andExpect(
                jsonPath("$.message")
                        .value("Success")
        ).andExpect(jsonPath("$.payload.secretKey").exists());
    }

    @Test
    public void getCreatedDevice_should_return_device() throws Exception {
        mockMvc.perform(
                post("/device")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                  "name": "My Refrigerator",
                                  "serial": 12312,
                                  "deviceType": "Refrigerator"
                                }""")
        );
        mockMvc.perform(get("/device/12312"))
                .andExpect(status().isOk()).andExpect(
                        jsonPath("$.payload.name")
                                .value("My Refrigerator")
                ).andExpect(jsonPath("$.payload.deviceType")
                        .value("Refrigerator"));
    }
}
