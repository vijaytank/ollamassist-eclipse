package com.localllama.plugin.autocomplete;

import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.util.ModelSelectorUtil;

public class LocalLlamaAutocompleteClient {

	// The ENDPOINT and payload construction logic is now handled in LocalLlamaClient
	// private static final String ENDPOINT = "http://localhost:11434/api/generate";

	public static String getSuggestion(String prefix) {
		String model = ModelSelectorUtil.getDefaultModel();
		// Use the new generateCompletion method in LocalLlamaClient
		String suggestion = LocalLlamaClient.generateCompletion(prefix, model);
		return suggestion;
	}
}