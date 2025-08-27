package com.localllama.plugin.preferences;

import com.localllama.plugin.LocalLlamaActivator;
import org.eclipse.jface.preference.IPreferenceStore;

public class LocalLlamaPreferenceStore {

    private static final String OLLAMA_ENDPOINT = "ollama_endpoint";
    private static final String DEFAULT_OLLAMA_ENDPOINT = "http://localhost:11434";
    private static final String LOGGING_ENABLED = "logging_enabled";
    private static final String SELECTED_MODEL = "selected_model";
	private static final String COMMIT_MESSAGE_PROMPT = "commit_message_prompt";
    private static final String SETUP_COMPLETE = "setupComplete";
    private static final String WORKSPACE_INDEXED = "workspaceIndexed";


    public static IPreferenceStore getStore() {
        return LocalLlamaActivator.getDefault().getPreferenceStore();
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

	public static String getCommitMessagePrompt() {
		return getStore().getString(COMMIT_MESSAGE_PROMPT);
	}

	public static void setCommitMessagePrompt(String prompt) {
		getStore().setValue(COMMIT_MESSAGE_PROMPT, prompt);
	}

    public static boolean isSetupComplete() {
        return getStore().getBoolean(SETUP_COMPLETE);
    }

    public static void setSetupComplete(boolean complete) {
        getStore().setValue(SETUP_COMPLETE, complete);
    }

    public static boolean isWorkspaceIndexed() {
        return getStore().getBoolean(WORKSPACE_INDEXED);
    }

    public static void setWorkspaceIndexed(boolean indexed) {
        getStore().setValue(WORKSPACE_INDEXED, indexed);
    }

    public static void save() {
        LocalLlamaActivator.getDefault().savePreferenceStore();
    }


    public static void initializeDefaults() {
        getStore().setDefault(OLLAMA_ENDPOINT, DEFAULT_OLLAMA_ENDPOINT);
        getStore().setDefault(LOGGING_ENABLED, false);
        getStore().setDefault(SELECTED_MODEL, "");
		getStore().setDefault(COMMIT_MESSAGE_PROMPT, "");
        getStore().setDefault(SETUP_COMPLETE, false);
        getStore().setDefault(WORKSPACE_INDEXED, false);
    }
}