package com.localllama.plugin.util;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.setup.ModelFetcher;
import java.util.List;

public class ModelSelectorUtil {

    public static String[] getModelNames() {
        List<String> models = ModelFetcher.getInstalledModels();
        return models.toArray(new String[0]);
    }

    public static void setSelectedModel(String modelName) {
        LocalLlamaPreferenceStore.setModel(modelName);
    }

    public static String getDefaultModel() {
        return LocalLlamaPreferenceStore.getModel();
    }
}
