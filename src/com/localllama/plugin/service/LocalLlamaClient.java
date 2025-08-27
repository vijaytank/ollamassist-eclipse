package com.localllama.plugin.service;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.ui.ChatMessage;
import com.localllama.plugin.util.LocalLlamaJsonUtil;
import com.localllama.plugin.util.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

public class LocalLlamaClient {

    public static void streamingQuery(List<ChatMessage> messages, String model, Consumer<String> chunkCallback, Runnable doneCallback) {
        String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/chat";
        Logger.log("Streaming query to " + endpoint + " with model " + model);

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(endpoint);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("model", model);
                payload.put("stream", true);

                JSONArray messagesJson = new JSONArray();
                for (ChatMessage message : messages) {
                    JSONObject messageJson = new JSONObject();
                    String role = message.getSender() == ChatMessage.SenderType.USER ? "user" : "assistant";
                    messageJson.put("role", role);
                    messageJson.put("content", message.getMessage());
                    messagesJson.put(messageJson);
                }
                payload.put("messages", messagesJson);

                Logger.log("Payload: " + payload.toString());

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes("UTF-8"));
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String chunk = LocalLlamaJsonUtil.parseStreamingResponse(line);
                        if (chunk != null && !chunk.isEmpty()) {
                            chunkCallback.accept(chunk);
                        }
                    }
                }
            } catch (Exception e) {
                Logger.error("Error during streaming query", e);
                chunkCallback.accept("Error: " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                doneCallback.run();
                Logger.log("Streaming query finished");
            }
        }).start();
    }

    public static String generateCompletion(String prefix, String model) {
        String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/generate";
        Logger.log("Generating completion for prefix: '" + prefix + "' with model " + model);

        HttpURLConnection conn = null;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("model", model);
            payload.put("prompt", prefix);
            payload.put("stream", false);

            Logger.log("Payload: " + payload.toString());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes("UTF-8"));
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return LocalLlamaJsonUtil.parseGenerationResponse(response.toString());
            }
        } catch (Exception e) {
            Logger.error("Error generating completion", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            Logger.log("Generation finished");
        }
    }
}
