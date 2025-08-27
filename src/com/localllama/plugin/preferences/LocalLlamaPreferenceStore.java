package com.localllama.plugin.preferences;

import com.localllama.plugin.LocalLlamaActivator;
import org.eclipse.jface.preference.IPreferenceStore;

public class LocalLlamaPreferenceStore {

    // The endpoint for the Ollama API
    private static final String OLLAMA_ENDPOINT = "ollama_endpoint";
    // The default endpoint for the Ollama API
    private static final String DEFAULT_OLLAMA_ENDPOINT = "http://localhost:11434";
    // Whether to enable logging
    private static final String LOGGING_ENABLED = "logging_enabled";
    // The currently selected model
    private static final String SELECTED_MODEL = "selected_model";
	// The prompt to use for generating commit messages
    private static final String COMMIT_MESSAGE_PROMPT = "commit_message_prompt";
    // The default prompt for generating commit messages
    private static final String DEFAULT_COMMIT_MESSAGE_PROMPT = "Generate a concise Conventional Commit message for the following Git diff. The message should follow the format: type(scope): description\\n\\n[optional body]\\n\\n[optional footer].";
    // Whether the setup wizard has been completed
    private static final String SETUP_COMPLETE = "setupComplete";
    // Whether the workspace has been indexed
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

    public static void initializeDefaults() {
        getStore().setDefault(OLLAMA_ENDPOINT, DEFAULT_OLLAMA_ENDPOINT);
        getStore().setDefault(LOGGING_ENABLED, false);
        getStore().setDefault(SELECTED_MODEL, "");
		getStore().setDefault(COMMIT_MESSAGE_PROMPT, DEFAULT_COMMIT_MESSAGE_PROMPT);
        getStore().setDefault(SETUP_COMPLETE, false);
        getStore().setDefault(WORKSPACE_INDEXED, false);
    }
}
