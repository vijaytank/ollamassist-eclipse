package com.ollamassist.plugin.autocomplete;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OllamaAutocompleteClient {

    private static final String ENDPOINT = "http://localhost:11434/api/generate";

    public static String getSuggestion(String prefix) {
        try {
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "{\"model\":\"llama3.1\",\"prompt\":\"" + escape(prefix) + "\",\"stream\":false}";
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

            return parseResponse(response.toString());
        } catch (Exception e) {
            return "";
        }
    }

    private static String escape(String input) {
        return input.replace("\"", "\\\"");
    }

    private static String parseResponse(String json) {
        int index = json.indexOf("\"response\":\"");
        if (index != -1) {
            int start = index + 11;
            int end = json.indexOf("\"", start);
            if (end > start) {
                return json.substring(start, end).replace("\\n", "\n");
            }
        }
        return "";
    }
}
