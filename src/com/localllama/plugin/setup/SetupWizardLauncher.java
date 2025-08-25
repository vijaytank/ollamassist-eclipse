package com.localllama.plugin.setup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

import com.localllama.plugin.rag.ProjectDependencyTracker;
import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.setup.SetupWizard;

public class SetupWizardLauncher implements IStartup {
    @Override
    public void earlyStartup() {
        Display.getDefault().asyncExec(() -> {
            ProjectDependencyTracker.indexWorkspace();
 LocalLlamaPreferenceStore.setWorkspaceIndexed(true);
 LocalLlamaPreferenceStore.save();

            if (!LocalLlamaPreferenceStore.isInitialized()) {
                SetupWizard.open();
            }
        });
    }
}
