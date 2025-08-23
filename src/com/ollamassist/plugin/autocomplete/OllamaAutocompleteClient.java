package com.ollamassist.plugin.autocomplete;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ollamassist.plugin.rag.ProjectDependencyTracker;

public class OllamaAutocompleteClient {

    private static final String ENDPOINT = "http://localhost:11434/api/generate";

    public static String getSuggestion(String prefix) {
        ProjectDependencyTracker.indexWorkspace();

        StringBuilder context = new StringBuilder();
        context.append("Here is the current code context:\n");
        context.append(prefix).append("\n\n");

        String prompt = "Based on this code, suggest the next line or completion:\n" + context;
        String payload = "{\"model\":\"llama3.1\",\"prompt\":\"" + escapeJson(prompt) + "\",\"stream\":false}";

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

            return parseResponse(response.toString());
        } catch (Exception e) {
            return "";
        }
    }

    private static String escapeJson(String input) {
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    private static String parseResponse(String json) {
        Pattern pattern = Pattern.compile("\"response\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).replace("\\n", "\n");
        }
        return "";
    }
}
