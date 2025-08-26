package com.localllama.plugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.localllama.plugin.LocalLlamaActivator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = LocalLlamaActivator.getDefault().getPreferenceStore();

		// Set literal defaults here; SetupConfig has been removed.
		store.setDefault("OLLAMA_ENDPOINT", "http://localhost:11434");
		store.setDefault("model", "llama3.1");
		store.setDefault("workspaceIndexed", false);

	}
}
