package com.example.LeaderITTestTask;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestUtils {
    private final MockMvc mockMvc;
    private final Gson gson;
    private HashMap<String, Device> tmpDevices;

    public TestUtils(MockMvc mockMvc) {
        tmpDevices = new HashMap<>();
        this.mockMvc = mockMvc;
        gson = Utils.createGson();
    }

    public ResultActions postDevice(String name, long serial, String type) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("serial", serial);
        jsonObject.put("type", type);
        return postJSON(jsonObject, "/device");
    }

    public ResultActions postEvent(
            String secretKey,
            String eventType,
            Long serial,
            Object payload
    ) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secretKey", secretKey);
        jsonObject.put("type", eventType);
        jsonObject.put("deviceSerial", serial);
        jsonObject.put("payload", payload);
        return postJSON(jsonObject, "/event");
    }

    public ResultActions postJSON(JSONObject content, String path) throws Exception {
        return mockMvc.perform(
                post(path)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(content.toString())
        );
    }

    public String getSecretKey(ResultActions resultActions)
            throws UnsupportedEncodingException, JSONException {
        String result = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();
        JSONObject jsonObject = new JSONObject(result);
        return jsonObject.getJSONObject("payload").getString("secretKey");
    }

    public ResultActions performGet(String path, String content) throws Exception {
        return mockMvc.perform(
                get(path).content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
    }

    public Device getDevice(Long serial) throws Exception {
        String content = performGet("/device/serial/" + serial, "")
                .andReturn()
                .getResponse()
                .getContentAsString();
        String devicePayload = new JSONObject(content).getJSONObject("payload").toString();
        return gson.fromJson(devicePayload, Device.class);
    }

    public String createNewTmpDevice(String type) throws Exception {
        long deviceIndex = tmpDevices.size();
        String secretKey = getSecretKey(
                postDevice("Device_" + deviceIndex, deviceIndex, type)
        );
        Device device = getDevice(deviceIndex);
        tmpDevices.put(secretKey, device);
        return secretKey;
    }

    public String createNewTmpDevice() throws Exception {
        return createNewTmpDevice("Type");
    }

    public void addEventToTmpDevice(String secret, String eventType) throws Exception {
        Device device = tmpDevices.get(secret);
        postEvent(secret, eventType, device.getSerial(), null);
    }

    public Device getTmpDevice(String secret) {
        return tmpDevices.get(secret);
    }

    public String getDatePageLimitContent(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            long page,
            String type
    ) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDateTime", startDateTime);
        jsonObject.put("endDateTime", endDateTime);
        jsonObject.put("page", page);
        jsonObject.put("type", type);
        return jsonObject.toString();
    }

    public JSONArray getPayloadAsArray(ResultActions resultActions)
            throws UnsupportedEncodingException,
            JSONException {
        String eventsString = resultActions.andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(eventsString);
        return jsonObject.getJSONArray("payload");
    }

    public JSONArray getPayloadAsArray(String path, String content) throws Exception {
        return getPayloadAsArray(
                performGet(path, content)
        );
    }

    public JSONArray getEvents(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            long page,
            String type,
            String secret
    ) throws Exception {
        String content = this.getDatePageLimitContent(startDateTime, endDateTime, page, type);
        String url = "/event/device/" + this.getTmpDevice(secret).getSerial();
        ResultActions resultActions = this.performGet(url, content);
        return this.getPayloadAsArray(resultActions);
    }

    public JSONObject getJson(String path, String content) throws Exception {
        return new JSONObject(
                performGet(path, content)
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        );
    }

    public void assertTmpDevice(JSONObject deviceJson, String tmpSecret) {
        Device tmpDevice = getTmpDevice(tmpSecret);
        Device deviceToAssert = gson.fromJson(String.valueOf(deviceJson), Device.class);
        assertEquals(tmpDevice.getType(), deviceToAssert.getType());
        assertEquals(tmpDevice.getId(), deviceToAssert.getId());
        assertEquals(tmpDevice.getCreationDate(), deviceToAssert.getCreationDate());
        assertEquals(tmpDevice.getSerial(), deviceToAssert.getSerial());
    }

    public ResultActions postEventForTmpDevice(String secret, String eventType) throws Exception {
        return postEvent(secret, eventType, getTmpDevice(secret).getSerial(), "asdasdd");
    }

    public JSONArray getEvents(int page, String type, String secret) throws Exception {
        return getEvents(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), page, type, secret);
    }
}
