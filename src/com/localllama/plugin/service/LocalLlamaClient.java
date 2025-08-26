package com.localllama.plugin.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.rag.ProjectDependencyTracker;
import com.localllama.plugin.ui.ChatMessage;
import com.localllama.plugin.util.LocalLlamaJsonUtil;
import com.localllama.plugin.util.LocalLlamaPromptUtil;
import com.localllama.plugin.util.SymbolExtractor;

/** Client for interacting with LocalLlama model */
public class LocalLlamaClient {

	public static String queryModel(String prompt, String model) {
		String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/generate";
		String payload = "{\"model\":\"" + model + "\",\"prompt\":\"" + LocalLlamaPromptUtil.escapeJson(prompt)
				+ "\",\"stream\":false}";
		try {
			return sendHttpRequest(endpoint, payload);
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	/** Streaming chat (POST /api/chat) */
	public static void streamChatCompletion(List<ChatMessage> messages, String model, Consumer<String> chunkCallback,
			Consumer<String> doneCallback) {

		String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/chat";

		// Build messages JSON
		StringBuilder messagesJson = new StringBuilder("[");
		for (int i = 0; i < messages.size(); i++) {
			ChatMessage message = messages.get(i);
			messagesJson.append(String.format("{\"role\":\"%s\",\"content\":\"%s\"}", message.getRole(),
					LocalLlamaPromptUtil.escapeJson(message.getContent())));
			if (i < messages.size() - 1) {
				messagesJson.append(", ");
			}
		}
		messagesJson.append("]");

		String payload = String.format("{\"model\":\"%s\",\"messages\":%s,\"stream\":true}", model, messagesJson);

		new Thread(() -> {
			try {
				URL url = new URL(endpoint);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(60000);

				// IMPORTANT: enable output BEFORE getOutputStream/write
				conn.setDoOutput(true);

				try (OutputStream os = conn.getOutputStream()) {
					os.write(payload.getBytes());
					os.flush();
				}

				conn.connect();

				InputStream stream = (conn.getResponseCode() >= 400) ? conn.getErrorStream() : conn.getInputStream();

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
					String line;
					StringBuilder fullResponse = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						String chunk = LocalLlamaJsonUtil.parseResponse(line);
						if (chunk != null) {
							fullResponse.append(chunk);
							// UI thread callback
							org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
								chunkCallback.accept(chunk);
							});
						}
					}
					// UI thread done callback
					String finalText = fullResponse.toString();
					org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
						doneCallback.accept(finalText);
					});
				} finally {
					conn.disconnect();
				}
			} catch (Exception e) {
				org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
					doneCallback.accept("Error: " + e.getMessage());
				});
			}
		}).start();
	}

	/** Blocking call (POST /api/generate) */
	public static String blockingQuery(String prompt, String model) {
		String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/generate";
		String payload = "{\"model\":\"" + model + "\",\"prompt\":\"" + LocalLlamaPromptUtil.escapeJson(prompt)
				+ "\",\"stream\":false}";
		try {
			return sendHttpRequest(endpoint, payload);
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	/** Autocomplete helper that builds a context and calls /api/generate */
	public static String generateCompletion(String prefix, String model) {
		ProjectDependencyTracker.indexWorkspace();
		Set<String> relatedFiles = new HashSet<>();
		for (String symbol : extractSymbols(prefix)) {
			relatedFiles.addAll(ProjectDependencyTracker.getFilesUsingSymbol(symbol));
		}

		StringBuilder context = new StringBuilder();
		for (String file : relatedFiles) {
			context.append("// File: ").append(file).append("\n");
			context.append(ProjectDependencyTracker.getFileContent(file)).append("\n\n");
		}

		// Limit context size to avoid exceeding model context window
		int maxContextSize = 2000;
		if (context.length() > maxContextSize) {
			context.setLength(maxContextSize);
		}

		String completionPrompt = "Based on this code context from my workspace:\n" + context
				+ "\nComplete the following code:\n" + prefix;

		String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/generate";
		String payload = "{\"model\":\"" + model + "\",\"prompt\":\""
				+ LocalLlamaPromptUtil.escapeJson(completionPrompt) + "\",\"stream\":false}";

		try {
			return sendHttpRequest(endpoint, payload);
		} catch (Exception e) {
			return ""; // Return empty string on error for autocomplete
		}
	}

	/** Shared POST helper (ensures doOutput=true before writing). */
	private static String sendHttpRequest(String endpoint, String payload) throws Exception {
		URL url = new URL(endpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(60000);

		// IMPORTANT: enable output before writing a body
		conn.setDoOutput(true);

		try (OutputStream os = conn.getOutputStream()) {
			os.write(payload.getBytes());
			os.flush();
		}

		conn.connect();

		InputStream stream = (conn.getResponseCode() >= 400) ? conn.getErrorStream() : conn.getInputStream();

		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} finally {
			conn.disconnect();
		}

		// Parse plain /api/generate responses
		return LocalLlamaPromptUtil.parseResponse(response.toString());
	}

	private static Set<String> extractSymbols(String prompt) {
		Set<String> relatedFiles = new HashSet<>();
		for (String symbol : SymbolExtractor.extractFromPrompt(prompt)) {
			relatedFiles.addAll(ProjectDependencyTracker.getFilesUsingSymbol(symbol));
		}
		return relatedFiles;
	}
}
