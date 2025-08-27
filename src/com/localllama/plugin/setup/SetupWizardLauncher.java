package com.localllama.plugin.setup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.rag.ProjectDependencyTracker;
import com.localllama.plugin.service.LocalLlamaClient;

public class SetupWizardLauncher implements IStartup {
	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(() -> {
			ProjectDependencyTracker.indexAllOpenProjects();
			LocalLlamaPreferenceStore.setWorkspaceIndexed(true);
			LocalLlamaPreferenceStore.save();

			boolean firstTime = !LocalLlamaPreferenceStore.isSetupComplete();
			boolean connectionFailed = !LocalLlamaClient.isEndpointReachable();

			if (firstTime || connectionFailed) {
				SetupWizard.open();
			}
		});
	}
}
