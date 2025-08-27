package com.localllama.plugin.setup;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.util.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ModelFetcher {

    public static List<String> getInstalledModels() {
        Logger.log("Fetching installed models...");
        List<String> models = new ArrayList<>();
        try {
            String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint();
            URL url = new URL(endpoint + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject json = new JSONObject(response.toString());
                JSONArray modelArray = json.getJSONArray("models");

                for (int i = 0; i < modelArray.length(); i++) {
                    JSONObject modelObj = modelArray.getJSONObject(i);
                    String modelName = modelObj.optString("name", ""); // Ollama API uses 'name'
                    if (!modelName.isEmpty()) {
                        models.add(modelName);
                    }
                }
                Logger.log("Found models: " + models);
            }
        } catch (Exception e) {
            Logger.error("Error fetching installed models", e);
        }

        // Fallback if none found
        if (models.isEmpty()) {
            Logger.log("No models found from API, using fallback list.");
            models.addAll(Arrays.asList("llama3.1", "codellama:latest", "mistral", "custom-model"));
        }

        return models;
    }
}
