package com.example.LeaderITTestTask;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestUtils {
    private final MockMvc mockMvc;
    private final Gson gson;
    private HashMap<String, Device> tmpDevices;

    public TestUtils(MockMvc mockMvc) {
        tmpDevices = new HashMap<>();
        this.mockMvc = mockMvc;
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(
                            JsonElement json,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext
                    ) throws JsonParseException {
                        return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
                    }
                }).create();
    }

    public ResultActions postDevice(String name, long serial, String deviceType) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("serial", serial);
        jsonObject.put("deviceType", deviceType);
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
        String content = performGet("/device/" + serial, "")
                .andReturn()
                .getResponse()
                .getContentAsString();
        String devicePayload = new JSONObject(content).getJSONObject("payload").toString();
        return gson.fromJson(devicePayload, Device.class);
    }

    public String createNewTmpDevice() throws Exception {
        long deviceIndex = tmpDevices.size();
        String secretKey = getSecretKey(
                postDevice("Device_" + deviceIndex, deviceIndex, "Type")
        );
        Device device = getDevice(deviceIndex);
        tmpDevices.put(secretKey, device);
        return secretKey;
    }

    public void addEventToTmpDevice(String secret, String eventType) throws Exception {
        Device device = tmpDevices.get(secret);
        postEvent(secret, eventType, device.getSerial(), null);
    }

    public Device getTmpDevice(String secret) throws Exception {
        return tmpDevices.get(secret);
    }

    public String getDatePageLimitContent(
            LocalDateTime earliestDate,
            LocalDateTime latestDate,
            long page,
            String type
    ) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("earliestDate", earliestDate);
        jsonObject.put("latestDate", latestDate);
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

    public JSONArray getEvents(
            LocalDateTime earliestDate,
            LocalDateTime latestDate,
            long page,
            String type,
            String secret
    ) throws Exception {
        String content = this.getDatePageLimitContent(earliestDate, latestDate, page, type);
        String url = "/event/device/" + this.getTmpDevice(secret).getSerial();
        ResultActions resultActions = this.performGet(url, content);
        return this.getPayloadAsArray(resultActions);
    }
}
