package com.ollamassist.plugin.preferences;

import org.eclipse.jface.preference.PreferenceStore;

import com.ollamassist.plugin.setup.SetupConfig;

public class OllamAssistPreferenceStore extends PreferenceStore {

	public OllamAssistPreferenceStore() {
		setDefault("model", SetupConfig.get("model"));
		setDefault("workspaceIndexed", Boolean.parseBoolean(SetupConfig.get("workspaceIndexed")));
	}
}
