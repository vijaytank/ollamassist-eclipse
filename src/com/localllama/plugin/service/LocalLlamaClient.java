package com.localllama.plugin.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

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

	public static void streamChatCompletion(List<ChatMessage> messages, String model, Consumer<String> chunkCallback,
			Consumer<String> doneCallback) {

		String endpoint = LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/chat";

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
							org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
								chunkCallback.accept(chunk);
							});
						}
					}
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
		StringBuilder context = new StringBuilder();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.isOpen()) {
				if (!ProjectDependencyTracker.isProjectIndexed(project)) {
					new Thread(() -> ProjectDependencyTracker.indexProject(project)).start();
				}
				for (String symbol : SymbolExtractor.extractFromPrompt(prefix)) {
					context.append(ProjectDependencyTracker.searchContent(project, symbol));
				}
			}
		}

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
			return "";
		}
	}

	private static String sendHttpRequest(String endpoint, String payload) throws Exception {
		URL url = new URL(endpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(60000);
		conn.setDoOutput(true);

		try (OutputStream os = conn.getOutputStream()) {
			os.write(payload.getBytes());
			os.flush();
		}

		conn.connect();

		InputStream stream = (conn.getResponseCode() >= 400) ? conn.getErrorStream() : conn.getInputStream();

		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			char[] buffer = new char[8192];
			int len;
			while ((len = reader.read(buffer)) != -1) {
				response.append(buffer, 0, len);
			}
		} finally {
			conn.disconnect();
		}

		return LocalLlamaPromptUtil.parseResponse(response.toString());
	}

	public static boolean isEndpointReachable() {
		try {
			URL url = new URL(LocalLlamaPreferenceStore.getOllamaEndpoint() + "/api/tags");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.connect();
			return conn.getResponseCode() == 200;
		} catch (Exception e) {
			return false;
		}
	}
}
