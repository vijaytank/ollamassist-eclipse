package com.localllama.plugin.autocomplete;

import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.util.ModelSelectorUtil;

public class LocalLlamaAutocompleteClient {

    public static String getSuggestion(String content) {
        String model = ModelSelectorUtil.getDefaultModel();
        return LocalLlamaClient.generateCompletion(content, model);
    }
}
