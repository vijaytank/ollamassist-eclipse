package com.localllama.plugin.preferences;

import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.localllama.plugin.setup.ModelFetcher;

public class LocalLlamaPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public LocalLlamaPreferencePage() {
		super(GRID);
		// Use the singleton instance, not the removed 'load()' static
		setPreferenceStore(LocalLlamaPreferenceStore.getInstance());
		setDescription("Configure LocalLlama plugin settings");
	}

	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor("OLLAMA_ENDPOINT", "Ollama Endpoint:", getFieldEditorParent()));

		List<String> models = ModelFetcher.getInstalledModels();
		String[][] modelOptions = models.stream().map(name -> new String[] { name, name }).toArray(String[][]::new);
		addField(new ComboFieldEditor("model", "Model:", modelOptions, getFieldEditorParent()));

		addField(new BooleanFieldEditor("workspaceIndexed", "Workspace Indexed", getFieldEditorParent()));
		addField(new StringFieldEditor("COMMIT_MESSAGE_PROMPT", "Commit Message Prompt:", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		LocalLlamaPreferenceStore.setModel(getPreferenceStore().getString("model"));
		LocalLlamaPreferenceStore.setWorkspaceIndexed(getPreferenceStore().getBoolean("workspaceIndexed"));
		LocalLlamaPreferenceStore.setOllamaEndpoint(getPreferenceStore().getString("OLLAMA_ENDPOINT"));
		LocalLlamaPreferenceStore.setCommitMessagePrompt(getPreferenceStore().getString("COMMIT_MESSAGE_PROMPT"));
		// Persist to disk
		LocalLlamaPreferenceStore.saveToDisk();
		return ok;
	}
}
