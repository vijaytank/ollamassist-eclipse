package com.localllama.plugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
//		IPreferenceStore store = LocalLlamaActivator.getDefault().getPreferenceStore();

		IPreferenceStore store = LocalLlamaPreferenceStore.getInstance();
		store.setDefault("setupComplete", false);

	}
}
