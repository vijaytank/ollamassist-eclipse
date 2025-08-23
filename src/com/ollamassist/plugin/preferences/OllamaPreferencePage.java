package com.ollamassist.plugin.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ollamassist.plugin.Activator;

public class OllamaPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public OllamaPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configure Ollama connection");
	}

	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor("OLLAMA_ENDPOINT", "Ollama Endpoint:", getFieldEditorParent()));
		addField(new StringFieldEditor("OLLAMA_MODEL", "Model Name:", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
