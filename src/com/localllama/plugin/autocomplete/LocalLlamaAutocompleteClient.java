package com.localllama.plugin.autocomplete;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.service.LocalLlamaClient;

public class LocalLlamaAutocompleteClient {

    public static String getSuggestion(String content) {
        String model = LocalLlamaPreferenceStore.getModel();
        String[] suggestions = LocalLlamaClient.generateCompletion(content, model);
        if (suggestions.length > 0) {
            return suggestions[0];
        }
        return null;
    }
}
