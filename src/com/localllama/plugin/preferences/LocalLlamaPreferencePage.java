package com.localllama.plugin.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class LocalLlamaPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public LocalLlamaPreferencePage() {
        super(GRID);
        setPreferenceStore(LocalLlamaPreferenceStore.getStore());
        setDescription("Configure LocalLlama settings");
    }

    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor("ollama_endpoint", "Ollama Endpoint:", getFieldEditorParent()));
        addField(new BooleanFieldEditor("logging_enabled", "Enable Logging", getFieldEditorParent()));
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
}
