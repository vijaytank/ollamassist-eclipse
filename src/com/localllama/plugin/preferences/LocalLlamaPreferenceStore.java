package com.localllama.plugin.preferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jface.preference.PreferenceStore;

public class LocalLlamaPreferenceStore extends PreferenceStore {
	private static volatile LocalLlamaPreferenceStore instance;

	private static final String CONFIG_FILE = ".localllama/localllama.properties";

	// Built-in defaults
	private static final String DEFAULT_OLLAMA_ENDPOINT = "http://localhost:11434";
	private static final String DEFAULT_MODEL = "llama3.1";
	private static final boolean DEFAULT_WORKSPACE_INDEXED = false;
	private static final String DEFAULT_COMMIT_PROMPT = "Generate a concise Conventional Commit message for the following Git diff. "
			+ "The message should follow the format: type(scope): description\n\n"
			+ "[optional body]\n\n[optional footer].";

	private LocalLlamaPreferenceStore() {
		// Seed default values on the in-memory store
		setDefault("OLLAMA_ENDPOINT", DEFAULT_OLLAMA_ENDPOINT);
		setDefault("model", DEFAULT_MODEL);
		setDefault("workspaceIndexed", DEFAULT_WORKSPACE_INDEXED);
		setDefault("COMMIT_MESSAGE_PROMPT", DEFAULT_COMMIT_PROMPT);
	}

	/** Singleton accessor: loads file if present and ensures defaults. */
	public static synchronized LocalLlamaPreferenceStore getInstance() {
		if (instance != null) {
			return instance;
		}

		instance = new LocalLlamaPreferenceStore();
		Path configPath = getConfigFilePath();

		// Load persisted values if the file exists
		if (Files.exists(configPath)) {
			try (FileInputStream fis = new FileInputStream(configPath.toFile())) {
				instance.load(fis); // instance method from PreferenceStore
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Ensure required keys are present (fallback to defaults)
		ensureKeyWithDefault("OLLAMA_ENDPOINT", DEFAULT_OLLAMA_ENDPOINT);
		ensureKeyWithDefault("model", DEFAULT_MODEL);
		ensureKeyWithDefault("workspaceIndexed", String.valueOf(DEFAULT_WORKSPACE_INDEXED));
		ensureKeyWithDefault("COMMIT_MESSAGE_PROMPT", DEFAULT_COMMIT_PROMPT);

		return instance;
	}

	/**
	 * Persist the current preferences to disk (uses instance
	 * save(OutputStream,...)).
	 */
	public static synchronized void saveToDisk() {
		if (instance == null) {
			System.err.println("Preference store not loaded. Cannot save.");
			return;
		}
		Path configPath = getConfigFilePath();
		try {
			Files.createDirectories(configPath.getParent());
			try (FileOutputStream fos = new FileOutputStream(configPath.toFile())) {
				instance.save(fos, null); // instance method from PreferenceStore
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// -------- Convenience getters/setters with sensible fallbacks --------

	public static String getModel() {
		if (instance == null) {
			getInstance();
		}
		String v = instance.getStoredString("model");
		return (v == null || v.isBlank()) ? DEFAULT_MODEL : v;
	}

	public static void setModel(String model) {
		if (instance == null) {
			getInstance();
		}
		instance.setValue("model", (model == null || model.isBlank()) ? DEFAULT_MODEL : model);
	}

	public static boolean isWorkspaceIndexed() {
		if (instance == null) {
			getInstance();
		}
		return instance.getStoredBoolean("workspaceIndexed");
	}

	public static void setWorkspaceIndexed(boolean indexed) {
		if (instance == null) {
			getInstance();
		}
		instance.setValue("workspaceIndexed", indexed);
	}

	public static String getOllamaEndpoint() {
		if (instance == null) {
			getInstance();
		}
		String v = instance.getStoredString("OLLAMA_ENDPOINT");
		return (v == null || v.isBlank()) ? DEFAULT_OLLAMA_ENDPOINT : v;
	}

	public static void setOllamaEndpoint(String endpoint) {
		if (instance == null) {
			getInstance();
		}
		instance.setValue("OLLAMA_ENDPOINT",
				(endpoint == null || endpoint.isBlank()) ? DEFAULT_OLLAMA_ENDPOINT : endpoint);
	}

	public static String getCommitMessagePrompt() {
		if (instance == null) {
			getInstance();
		}
		String v = instance.getStoredString("COMMIT_MESSAGE_PROMPT");
		return (v == null || v.isBlank()) ? DEFAULT_COMMIT_PROMPT : v;
	}

	public static void setCommitMessagePrompt(String prompt) {
		if (instance == null) {
			getInstance();
		}
		instance.setValue("COMMIT_MESSAGE_PROMPT",
				(prompt == null || prompt.isBlank()) ? DEFAULT_COMMIT_PROMPT : prompt);
	}

	public static boolean isInitialized() {
		return getConfigFilePath().toFile().exists();
	}

	private static Path getConfigFilePath() {
		String userHome = System.getProperty("user.home");
		return Paths.get(userHome, CONFIG_FILE);
	}

	private static void ensureKeyWithDefault(String key, String defaultValue) {
		String existing = instance.getString(key);
		if (existing == null || existing.isEmpty()) {
			instance.setValue(key, defaultValue);
		}
	}

	// Instance passthrough to avoid hiding PreferenceStore methods
	public String getStoredString(String name) {
		return super.getString(name);
	}

	public boolean getStoredBoolean(String name) {
		return super.getBoolean(name);
	}
}
