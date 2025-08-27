package com.localllama.plugin.util;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class ModelSelectorUtil {

    public static String[] getModelNames() {
        try {
            URL url = new URL(LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/tags");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                JSONObject json = new JSONObject(content.toString());
                JSONArray models = json.getJSONArray("models");
                String[] modelNames = new String[models.length()];
                for (int i = 0; i < models.length(); i++) {
                    modelNames[i] = models.getJSONObject(i).getString("name");
                }
                return modelNames;
            }
        } catch (Exception e) {
            Logger.error("Failed to get model names", e);
            return new String[0];
        }
    }
}
