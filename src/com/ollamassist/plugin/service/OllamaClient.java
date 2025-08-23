package com.ollamassist.plugin.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaClient {

    private static final String ENDPOINT = "http://localhost:11434/api/generate";

    public static String queryModel(String prompt) {
        try {
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "{\"model\":\"llama3.1\",\"prompt\":\"" + escape(prompt) + "\",\"stream\":false}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            InputStream stream = (conn.getResponseCode() >= 400)
                ? conn.getErrorStream()
                : conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
//            System.out.println("Raw response: " + response.toString());
            return parseResponse(response.toString());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String parseResponse(String json) {
        Pattern pattern = Pattern.compile("\"response\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).replace("\\n", "\n");
        }
        return "⚠️ No response received from Ollama.";
    }

    private static String escape(String input) {
        return input.replace("\"", "\\\"");
    }
}
