package com.ollamassist.plugin.setup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SetupConfig {

	private static final String CONFIG_DIR_NAME = ".ollamassist";
	private static final String CONFIG_FILE_NAME = "ollamassist.properties";
	private static final Properties props = new Properties();

	static {
		load();
	}

	public static boolean isInitialized() {
		return getConfigFilePath().toFile().exists();
	}

	public static String get(String key) {
		return props.getProperty(key);
	}

	public static void set(String key, String value) {
		props.setProperty(key, value);
	}

	public static void save() {
		Path configPath = getConfigFilePath();

		try {
			Files.createDirectories(configPath.getParent());
			try (FileOutputStream out = new FileOutputStream(configPath.toFile())) {
				props.store(out, "OllamAssist Configuration");
				System.out.println("Config saved to: " + configPath.toAbsolutePath());
			}
		} catch (IOException e) {
			System.err.println("Failed to save config: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void load() {
		Path configPath = getConfigFilePath();
		if (Files.exists(configPath)) {
			try (FileInputStream in = new FileInputStream(configPath.toFile())) {
				props.load(in);
			} catch (IOException e) {
				System.err.println("Failed to load config: " + e.getMessage());
			}
		}
	}

	private static Path getConfigFilePath() {
		String userHome = System.getProperty("user.home");
		return Paths.get(userHome, CONFIG_DIR_NAME, CONFIG_FILE_NAME);
	}
}
