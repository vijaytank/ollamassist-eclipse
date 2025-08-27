package com.localllama.plugin.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.localllama.plugin.util.Logger;
import com.localllama.plugin.util.ModelSelectorUtil;

public class LocalLlamaPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public LocalLlamaPreferencePage() {
		super(GRID);
		setPreferenceStore(LocalLlamaPreferenceStore.getStore());
		setDescription("Configure LocalLlama settings");
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor("ollama_endpoint", "Ollama Endpoint:", getFieldEditorParent()));
		addField(new ComboFieldEditor("model_name", "Model:", getModelNames(), getFieldEditorParent()));
		addField(new BooleanFieldEditor("logging_enabled", "Enable Logging", getFieldEditorParent()));
	}

	private String[][] getModelNames() {
		String[] models = ModelSelectorUtil.getModelNames();
		String[][] modelNames = new String[models.length][2];
		for (int i = 0; i < models.length; i++) {
			modelNames[i] = new String[] { models[i], models[i] };
		}
		return modelNames;
	}

	@Override
	public void init(IWorkbench workbench) {
		// No-op
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		LocalLlamaPreferenceStore.initializeDefaults();
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		Logger.updateLoggingPreference();
		return result;
	}
}
