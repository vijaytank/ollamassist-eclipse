package com.localllama.plugin.preferences;

import org.eclipse.jface.preference.PreferenceStore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class LocalLlamaPreferenceStore extends PreferenceStore {
	private static LocalLlamaPreferenceStore instance;

	private static final String CONFIG_FILE = ".localllama/localllama.properties";
	private static final String DEFAULT_COMMIT_PROMPT = "Generate a concise Conventional Commit message for the following Git diff. The message should follow the format: type(scope): description\n\n[optional body]\n\n[optional footer].";

	private LocalLlamaPreferenceStore() {
		// Defaults can be set here or loaded from a default properties file
		// For now, keep them minimal or load from a default source if needed
	}

	public static LocalLlamaPreferenceStore load() {
		if (instance == null) {
			instance = new LocalLlamaPreferenceStore();
			Path configPath = getConfigFilePath();
			try (FileInputStream fis = new FileInputStream(configPath.toFile())) {
				instance.load(fis);
				// Set default value if preference is not found
				if (!instance.contains("COMMIT_MESSAGE_PROMPT")) {
					instance.setDefault("COMMIT_MESSAGE_PROMPT", DEFAULT_COMMIT_PROMPT);
					instance.setValue("COMMIT_MESSAGE_PROMPT", DEFAULT_COMMIT_PROMPT);
				}
			} catch (FileNotFoundException e) {
				// Configuration file doesn't exist yet, which is fine on first run
				System.out.println("Configuration file not found, using defaults.");
			} catch (IOException e) {
				e.printStackTrace(); // Log the error
			}
		}
		return instance;
	}

	public static void save() {
		if (instance == null) {
			System.err.println("Preference store not loaded. Cannot save.");
			return;
		}
		Path configPath = getConfigFilePath();
		try {
			Files.createDirectories(configPath.getParent());
			try (FileOutputStream fos = new FileOutputStream(configPath.toFile())) {
				instance.save(fos, null);
			}
		} catch (IOException e) {
			e.printStackTrace(); // Log the error
		}
	}

	public static String getModel() {
		if (instance == null) {
			load(); // Load if not already loaded
		}
		return instance.getString("model");
	}

	public static void setModel(String model) {
		if (instance == null) {
			load();
		}
		instance.setValue("model", model);
	}

	public static boolean isWorkspaceIndexed() {
		if (instance == null) {
			load();
		}
		return instance.getBoolean("workspaceIndexed");
	}

	public static void setWorkspaceIndexed(boolean indexed) {
		if (instance == null) {
			load();
		}
		instance.setValue("workspaceIndexed", indexed);
	}

	public static String getOllamaEndpoint() {
		if (instance == null) {
			load();
		}
		return instance.getString("OLLAMA_ENDPOINT");
	}

	public static void setOllamaEndpoint(String endpoint) {
		if (instance == null) {
			load();
		}
		instance.setValue("OLLAMA_ENDPOINT", endpoint);
	}

	public static String getCommitMessagePrompt() {
		if (instance == null) {
			load();
		}
		return instance.getString("COMMIT_MESSAGE_PROMPT");
	}

	public static void setCommitMessagePrompt(String prompt) {
		if (instance == null) {
			load();
		}
		instance.setValue("COMMIT_MESSAGE_PROMPT", prompt);
	}

	public static boolean isInitialized() {
		return getConfigFilePath().toFile().exists();
	}

	private static Path getConfigFilePath() {
		String userHome = System.getProperty("user.home");
		return Paths.get(userHome, CONFIG_FILE);
	}
}