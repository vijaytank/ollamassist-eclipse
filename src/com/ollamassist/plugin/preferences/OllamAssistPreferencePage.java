package com.ollamassist.plugin.preferences;

import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ollamassist.plugin.setup.ModelFetcher;
import com.ollamassist.plugin.setup.SetupConfig;

public class OllamAssistPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public OllamAssistPreferencePage() {
		super(GRID);
		setPreferenceStore(new OllamAssistPreferenceStore());
		setDescription("Configure OllamAssist plugin settings");
	}

	@Override
	public void createFieldEditors() {
		List<String> models = ModelFetcher.getInstalledModels();
		String[][] modelOptions = models.stream().map(name -> new String[] { name, name }).toArray(String[][]::new);

		addField(new ComboFieldEditor("model", "Model:", modelOptions, getFieldEditorParent()));

		addField(new BooleanFieldEditor("workspaceIndexed", "Workspace Indexed", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// No-op
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		SetupConfig.set("model", getPreferenceStore().getString("model"));
		SetupConfig.set("workspaceIndexed", Boolean.toString(getPreferenceStore().getBoolean("workspaceIndexed")));
		SetupConfig.save();
		return ok;
	}
}
