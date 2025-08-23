package com.ollamassist.plugin.setup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

public class SetupWizardLauncher implements IStartup {
	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(() -> {
			if (!SetupConfig.isInitialized()) {
				SetupWizard.open();
			}
		});
	}
}
