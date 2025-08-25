package com.localllama.plugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.localllama.plugin.LocalLlamaActivator;
import com.localllama.plugin.setup.SetupConfig;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = LocalLlamaActivator.getDefault().getPreferenceStore();
		store.setDefault("OLLAMA_ENDPOINT", SetupConfig.get("OLLAMA_ENDPOINT"));
		store.setDefault("model", SetupConfig.get("model"));
		store.setDefault("workspaceIndexed", Boolean.parseBoolean(SetupConfig.get("workspaceIndexed")));
	}
}
