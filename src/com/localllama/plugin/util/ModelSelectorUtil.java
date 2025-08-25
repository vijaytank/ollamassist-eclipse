package com.localllama.plugin.util;

import java.util.List;

import com.localllama.plugin.setup.ModelFetcher;
import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;

public class ModelSelectorUtil {

	public static String[] getModelNames() {
		List<String> models = ModelFetcher.getInstalledModels();
		return models.toArray(new String[0]);
	}

	public static void setSelectedModel(String modelName) {
		LocalLlamaPreferenceStore.setModel(modelName);
		LocalLlamaPreferenceStore.save();
	}

	public static String getDefaultModel() {
		return LocalLlamaPreferenceStore.getModel();
	}
}
