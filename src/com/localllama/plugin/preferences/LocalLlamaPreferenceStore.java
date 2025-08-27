package com.localllama.plugin.preferences;

import com.localllama.plugin.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public class LocalLlamaPreferenceStore {

    private static final String OLLAMA_ENDPOINT = "ollama_endpoint";
    private static final String DEFAULT_OLLAMA_ENDPOINT = "http://localhost:11434";
    private static final String LOGGING_ENABLED = "logging_enabled";
    private static final String SELECTED_MODEL = "selected_model";

    public static IPreferenceStore getStore() {
        return Activator.getDefault().getPreferenceStore();
    }

    public static String getOllamaEndpoint() {
        return getStore().getString(OLLAMA_ENDPOINT);
    }

    public static void setOllamaEndpoint(String endpoint) {
        getStore().setValue(OLLAMA_ENDPOINT, endpoint);
    }

    public static String getDefaultOllamaEndpoint() {
        return DEFAULT_OLLAMA_ENDPOINT;
    }

    public static boolean isLoggingEnabled() {
        return getStore().getBoolean(LOGGING_ENABLED);
    }

    public static void setLoggingEnabled(boolean enabled) {
        getStore().setValue(LOGGING_ENABLED, enabled);
    }

    public static String getModel() {
        return getStore().getString(SELECTED_MODEL);
    }

    public static void setModel(String model) {
        getStore().setValue(SELECTED_MODEL, model);
    }

    public static void initializeDefaults() {
        getStore().setDefault(OLLAMA_ENDPOINT, DEFAULT_OLLAMA_ENDPOINT);
        getStore().setDefault(LOGGING_ENABLED, false);
        getStore().setDefault(SELECTED_MODEL, "");
    }
}
