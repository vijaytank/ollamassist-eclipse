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
import com.localllama.plugin.util.LocalLlamaPromptUtil;
import com.localllama.plugin.util.SymbolExtractor;
import com.localllama.plugin.util.LocalLlamaJsonUtil;

// Client for interacting with LocalLlama model

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

	public static void streamChatCompletion(List<ChatMessage> messages, String model, Consumer<String> chunkCallback, Consumer<String> doneCallback) {
		String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/chat"; // Use /api/chat for messages format

		StringBuilder messagesJson = new StringBuilder("[");
		for (int i = 0; i < messages.size(); i++) {
			ChatMessage message = messages.get(i);
			messagesJson.append(String.format("{\"role\":\"%s\", \"content\":\"%s\"}", message.getRole(), LocalLlamaPromptUtil.escapeJson(message.getContent())));
			if (i < messages.size() - 1) {
				messagesJson.append(", ");
			}
		}
		messagesJson.append("]");

		String payload = String.format("{\"model\":\"%s\",\"messages\":%s,\"stream\":true}", model, messagesJson.toString());

		new Thread(() -> {
			try {
				URL url = new URL(endpoint);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setDoOutput(true);

				try (OutputStream os = conn.getOutputStream()) {
					os.write(payload.getBytes());
					os.flush();
				}

				conn.connect();

				InputStream stream = (conn.getResponseCode() >= 400) ? conn.getErrorStream() : conn.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String line;
				StringBuilder fullResponse = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					String chunk = LocalLlamaJsonUtil.parseStreamingChatResponse(line); // Need a new parsing method in LocalLlamaJsonUtil
					if (chunk != null) {
						fullResponse.append(chunk);
						// Run callback on UI thread
						org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
							chunkCallback.accept(chunk);
						});
					}
				}
				reader.close();
				// Run callback on UI thread
				org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
					doneCallback.accept(fullResponse.toString());
				});

			} catch (Exception e) {
				// Run callback on UI thread
				org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
					doneCallback.accept("Error: " + e.getMessage());
				});
			}
		}).start();
	}

	// This method is kept for blocking calls like commit message generation
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

		// Limit context size to avoid exceeding model's context window
		int maxContextSize = 2000; 
		if (context.length() > maxContextSize) {
			context.setLength(maxContextSize);}
		String completionPrompt = "Based on this code context from my workspace:\n" + context + "\nComplete the following code:\n" + prefix;
		String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/generate";
		String payload = "{\"model\":\"" + model + "\",\"prompt\":\"" + LocalLlamaPromptUtil.escapeJson(completionPrompt)
				+ "\",\"stream\":false}";

		try {
			return sendHttpRequest(endpoint, payload);
		} catch (Exception e) {
			return ""; // Return empty string on error for autocomplete
		}
	}

	private static String sendHttpRequest(String endpoint, String payload) throws Exception { // This method now takes the endpoint as a parameter
			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			try (OutputStream os = conn.getOutputStream()) {
				os.write(payload.getBytes());
				os.flush();
			}
			conn.connect(); // Ensure connection is established before reading response

			InputStream stream = (conn.getResponseCode() >= 400) ? conn.getErrorStream() : conn.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			return LocalLlamaPromptUtil.parseResponse(response.toString()); // Use the utility to parse the response
		} finally {
			// No explicit disconnect needed in try-with-resources for streams
		}
	} // Corrected closing brace for sendHttpRequest

	// Simple class to represent chat messages
	public static class ChatMessage {
		private String role;
		private String content;

		public ChatMessage(String role, String content) {
			this.role = role;
			this.content = content;
		}

		public String getRole() {
			return role;
		}

		public String getContent() {
			return content;
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