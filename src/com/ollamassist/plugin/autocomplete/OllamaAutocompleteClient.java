package com.ollamassist.plugin.autocomplete;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ollamassist.plugin.rag.ProjectDependencyTracker;
import com.ollamassist.plugin.util.OllamaPromptUtil;

public class OllamaAutocompleteClient {

	private static final String ENDPOINT = "http://localhost:11434/api/generate";

	public static String getSuggestion(String prefix) {
		ProjectDependencyTracker.indexWorkspace();

		StringBuilder context = new StringBuilder();
		context.append("Here is the current code context:\n");
		context.append(prefix).append("\n\n");

		String prompt = "Based on this code, suggest the next line or completion:\n" + context;
		String payload = "{\"model\":\"llama3.1\",\"prompt\":\"" + OllamaPromptUtil.escapeJson(prompt)
				+ "\",\"stream\":false}";

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(ENDPOINT).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(payload.getBytes());
				os.flush();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			return OllamaPromptUtil.parseResponse(response.toString());
		} catch (Exception e) {
			return "";
		}
	}

}
