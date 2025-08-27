package com.localllama.plugin.util;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalLlamaJsonUtil {

    public static String parseStreamingResponse(String line) {
        try {
            JSONObject json = new JSONObject(line);
            if (json.has("message")) {
                JSONObject message = json.getJSONObject("message");
                if (message.has("content")) {
                    return message.getString("content");
                }
            }
            if (json.has("response")) {
                return json.getString("response");
            }
        } catch (JSONException e) {
            // Not a JSON object, likely just a string chunk
            return line; // Return the line itself if it's not JSON
        }
        return "";
    }

    public static String parseGenerationResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            if (json.has("response")) {
                return json.getString("response");
            }
        } catch (Exception e) {
            Logger.error("Error parsing generation response", e);
        }
        return null;
    }
}
