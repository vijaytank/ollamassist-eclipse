package com.ollamassist.plugin.setup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

import com.ollamassist.plugin.rag.ProjectDependencyTracker;

public class SetupWizardLauncher implements IStartup {
    @Override
    public void earlyStartup() {
        Display.getDefault().asyncExec(() -> {
            ProjectDependencyTracker.indexWorkspace();
            SetupConfig.set("workspaceIndexed", "true");
            SetupConfig.save();

            if (!SetupConfig.isInitialized()) {
                SetupWizard.open();
            }
        });
    }
}
