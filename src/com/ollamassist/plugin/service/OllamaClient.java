package com.ollamassist.plugin.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.ollamassist.plugin.rag.ProjectDependencyTracker;
import com.ollamassist.plugin.util.OllamaPromptUtil;
import com.ollamassist.plugin.util.SymbolExtractor;

public class OllamaClient {

	private static final String ENDPOINT = "http://localhost:11434/api/generate";

	public static String queryModel(String prompt) {
		ProjectDependencyTracker.indexWorkspace();

		Set<String> relatedFiles = new HashSet<>();
		for (String symbol : extractSymbols(prompt)) {
			relatedFiles.addAll(ProjectDependencyTracker.getFilesUsingSymbol(symbol));
		}

		StringBuilder context = new StringBuilder();
		for (String file : relatedFiles) {
			context.append("// File: ").append(file).append("\n");
			context.append(ProjectDependencyTracker.getFileContent(file)).append("\n\n");
		}

		String fullPrompt = "Use this context from my workspace:\n" + context + "\nQuestion:\n" + prompt;
		String payload = "{\"model\":\"llama3.1\",\"prompt\":\"" + OllamaPromptUtil.escapeJson(fullPrompt)
				+ "\",\"stream\":false}";

		try {
			URL url = new URL(ENDPOINT);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(payload.getBytes());
				os.flush();
			}

			InputStream stream = (conn.getResponseCode() >= 400) ? conn.getErrorStream() : conn.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

//            System.out.println("Raw response: " + response.toString());
			return OllamaPromptUtil.parseResponse(response.toString());
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	private static Set<String> extractSymbols(String prompt) {
		Set<String> relatedFiles = new HashSet<>();
		for (String symbol : SymbolExtractor.extractFromPrompt(prompt)) {
			relatedFiles.addAll(ProjectDependencyTracker.getFilesUsingSymbol(symbol));
		}
		return relatedFiles;
	}
}
