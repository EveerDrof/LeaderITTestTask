package com.example.LeaderITTestTask;

import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestUtils {
    private final MockMvc mockMvc;
    private final Gson gson;

    public TestUtils(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
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
            Long deviceId,
            Object payload
    ) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secretKey", secretKey);
        jsonObject.put("eventType", eventType);
        jsonObject.put("deviceId", deviceId);
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

    public ResultActions performGet(String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(path));
    }

    public Device getDevice(Long serial) throws Exception {
        String content = performGet("/device/" + serial)
                .andReturn()
                .getResponse()
                .getContentAsString();
        String devicePayload = new JSONObject(content).getJSONObject("payload").toString();
        return gson.fromJson(devicePayload, Device.class);
    }
}
