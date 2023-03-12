package com.example.LeaderITTestTask;

import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class LeaderItTestTaskApplicationTests {

    private TestUtils utils;
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeAll
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        this.utils = new TestUtils(mockMvc);
    }

    @Test
    public void addNewDevice_should_return_200() throws Exception {
        utils.postDevice("aaa", 0, "asd")
                .andExpect(status().isOk()).andExpect(
                        jsonPath("$.message")
                                .value("Success")
                ).andExpect(jsonPath("$.payload.secretKey").exists());
    }

    @Test
    public void getCreatedDevice_should_return_device() throws Exception {
        utils.postDevice("My Refrigerator", 12312, "Refrigerator");
        mockMvc.perform(get("/device/serial/12312"))
                .andExpect(status().isOk()).andExpect(
                        jsonPath("$.payload.name")
                                .value("My Refrigerator")
                ).andExpect(jsonPath("$.payload.type")
                        .value("Refrigerator"))
                .andExpect(jsonPath("$.payload.creationDate").exists());
    }

    @Test
    public void createDevicesWithSameSerial_should_return_400() throws Exception {
        utils.postDevice("My Refrigerator", 12312, "Refrigerator");
        utils.postDevice("My Refrigerator", 12312, "Refrigerator")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("This serial already exists")
                );
    }

    @Test
    public void nonExistentSerialDeviceGetCheck_should_return_404() throws Exception {
        mockMvc.perform(get("/device/serial/291"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No device with this serial")
                );
    }

    @Test
    public void postCorrectEvent_should_return_200() throws Exception {
        String key = utils.getSecretKey(
                utils.postDevice("Device", 1L, "Type")
        );
        Device device = utils.getDevice(1L);
        utils.postEvent(key, "EventType", device.getSerial(), null)
                .andExpect(status().isOk());
    }

    @Test
    public void postIncorrectSecretKey_should_return_401() throws Exception {
        utils.postDevice("Device", 1L, "Type");
        Device device = utils.getDevice(1L);
        utils.postEvent("Incorrect Key", "EventType", device.getSerial(), null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Incorrect key"));
    }

    @Test
    public void getEventsForDevice_should_return_200_and_events_list() throws Exception {
        LocalDateTime beforeAdding = LocalDateTime.now();
        String secret = utils.createNewTmpDevice();
        utils.addEventToTmpDevice(secret, "A");
        utils.addEventToTmpDevice(secret, "A");
        String content = utils.getDatePageLimitContent(beforeAdding, LocalDateTime.now(), 0, "A");
        String url = "/event/device/" + utils.getTmpDevice(secret).getSerial();
        ResultActions resultActions = utils.performGet(url, content);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.payload").isArray());
        assertEquals(2, utils.getPayloadAsArray(resultActions).length());
    }

    @Test
    public void getEventsBetweenEarliestAndLatest_should_return_200_and_events_list() throws Exception {
        String secret = utils.createNewTmpDevice();
        utils.addEventToTmpDevice(secret, "A");
        LocalDateTime startDate = LocalDateTime.now();
        Thread.sleep(50);
        utils.addEventToTmpDevice(secret, "B");
        utils.addEventToTmpDevice(secret, "C");
        Thread.sleep(50);
        LocalDateTime endDate = LocalDateTime.now();
        utils.addEventToTmpDevice(secret, "E");
        JSONArray jsonArray = utils.getEvents(startDate, endDate, 0, null, secret);
        assertEquals(2, jsonArray.length());
        assertEquals("B", jsonArray.getJSONObject(0).getString("type"));
        assertEquals("C", jsonArray.getJSONObject(1).getString("type"));
    }

    @Test
    public void getEventsByType_should_should_return_200_and_events_list() throws Exception {
        String secret = utils.createNewTmpDevice();
        utils.addEventToTmpDevice(secret, "A");
        Thread.sleep(50);
        LocalDateTime startDate = LocalDateTime.now();
        utils.addEventToTmpDevice(secret, "A");
        utils.addEventToTmpDevice(secret, "B");
        utils.addEventToTmpDevice(secret, "C");
        utils.addEventToTmpDevice(secret, "A");
        Thread.sleep(50);
        LocalDateTime endDate = LocalDateTime.now();
        Thread.sleep(50);
        utils.addEventToTmpDevice(secret, "A");
        JSONArray jsonArray = utils.getEvents(startDate, endDate, 0, "A", secret);
        assertEquals(2, jsonArray.length());
    }

    @Test
    public void eventPaginationTest_should_return_200_and_paginated_events_list() throws Exception {
        String secret = utils.createNewTmpDevice();
        for (int i = 0; i < 63; i++) {
            utils.addEventToTmpDevice(secret, "Y");
        }
        JSONArray events =
                utils.getEvents(
                        LocalDateTime.now().minusHours(1),
                        LocalDateTime.now().plusHours(1),
                        0,
                        "Y",
                        secret
                );
        assertEquals(50, events.length());
        events =
                utils.getEvents(
                        LocalDateTime.now().minusHours(1),
                        LocalDateTime.now().plusHours(1),
                        1,
                        "Y",
                        secret
                );
        assertEquals(13, events.length());
    }

    @Test
    public void getActiveDevicesTest_should_return_devices_with_recent_events() throws Exception {
        String secret = utils.createNewTmpDevice();
        String secret2 = utils.createNewTmpDevice();
        String secret3 = utils.createNewTmpDevice();
        JSONObject jsonObject = utils.getJson("/device/active", "");
        assertEquals(0, jsonObject.getJSONArray("payload").length());
        utils.postEvent(secret, "A", utils.getTmpDevice(secret).getSerial(), null);
        jsonObject = utils.getJson("/device/active", "");
        assertEquals(1, jsonObject.getJSONArray("payload").length());
        utils.postEvent(secret2, "A", utils.getTmpDevice(secret2).getSerial(), null);
        jsonObject = utils.getJson("/device/active", "");
        assertEquals(2, jsonObject.getJSONArray("payload").length());
        utils.postEvent(secret3, "A", utils.getTmpDevice(secret3).getSerial(), null);
        jsonObject = utils.getJson("/device/active", "");
        assertEquals(3, jsonObject.getJSONArray("payload").length());
    }

    @Test
    public void getAllDevices_should_return_all_devices() throws Exception {
        String secret0 = utils.createNewTmpDevice("0");
        String secret1 = utils.createNewTmpDevice("1");
        String secret2 = utils.createNewTmpDevice("2");
        String query = utils.getDatePageLimitContent(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                0,
                null
        );
        JSONArray jsonArray = utils.getPayloadAsArray("/device/all", query);
        utils.assertTmpDevice(jsonArray.getJSONObject(0), secret0);
        utils.assertTmpDevice(jsonArray.getJSONObject(1), secret1);
        utils.assertTmpDevice(jsonArray.getJSONObject(2), secret2);
    }

    @Test
    public void getDevicesByType_should_return_all_devices() throws Exception {
        String secret0 = utils.createNewTmpDevice("0");
        String secret1 = utils.createNewTmpDevice("1");
        String secret2 = utils.createNewTmpDevice("2");
        String query = utils.getDatePageLimitContent(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                0,
                "2"
        );
        JSONArray jsonArray = utils.getPayloadAsArray("/device/all", query);
        assertEquals(1, jsonArray.length());
        utils.assertTmpDevice(jsonArray.getJSONObject(0), secret2);
    }

    @Test
    public void postEventWithoutType_should_return_400_() throws Exception {
        String secret = utils.createNewTmpDevice("AA");
        ResultActions resultActions = utils.postEventForTmpDevice(secret, null);
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Type is required"));
    }
}
