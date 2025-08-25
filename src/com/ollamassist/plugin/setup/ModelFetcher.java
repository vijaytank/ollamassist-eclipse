package com.ollamassist.plugin.setup;

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
		List<String> models = new ArrayList<>();
		try {
			URL url = new URL("http://localhost:11434/api/tags");
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
					String modelName = modelObj.optString("model", modelObj.optString("name", ""));
					if (!modelName.isEmpty()) {
						models.add(modelName);
					}
				}

			}
		} catch (Exception e) {
			System.err.println("[ModelFetcher] Error: " + e.getMessage());
		}

		// Fallback if none found
		if (models.isEmpty()) {
			models.addAll(Arrays.asList("llama3.1", "codellama:latest", "mistral", "custom-model"));
		}

		return models;
	}
}
