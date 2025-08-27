package com.localllama.plugin.autocomplete;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.service.LocalLlamaClient;

public class LocalLlamaAutocompleteClient {

    public static String getSuggestion(String content) {
        String model = LocalLlamaPreferenceStore.getModel();
        return LocalLlamaClient.generateCompletion(content, model);
    }
}
