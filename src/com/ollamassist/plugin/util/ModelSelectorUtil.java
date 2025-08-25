package com.ollamassist.plugin.util;

import java.util.List;

import com.ollamassist.plugin.setup.ModelFetcher;
import com.ollamassist.plugin.setup.SetupConfig;

public class ModelSelectorUtil {

	public static String[] getModelNames() {
		List<String> models = ModelFetcher.getInstalledModels();
		return models.toArray(new String[0]);
	}

	public static void setSelectedModel(String modelName) {
		SetupConfig.set("model", modelName);
		SetupConfig.save();
	}

	public static String getDefaultModel() {
		String[] models = getModelNames();
		return models.length > 0 ? models[0] : "llama3.1";
	}
}
