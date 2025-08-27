package com.localllama.plugin.autocomplete;

import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.util.ModelSelectorUtil;

public class LocalLlamaAutocompleteClient {
	public static String getSuggestion(String prefix) {
		String model = ModelSelectorUtil.getDefaultModel();
		return LocalLlamaClient.generateCompletion(prefix, model);
	}
}
