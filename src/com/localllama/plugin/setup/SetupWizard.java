package com.localllama.plugin.setup;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;

public class SetupWizard extends Wizard {

	public SetupWizard() {
		setWindowTitle("LocalLlama Setup");
	}

	@Override
	public void addPages() {
		addPage(new ModelSelectionPage());
		addPage(new WorkspaceIndexPage());
		addPage(new FinalConfirmationPage());
	}

	@Override
	public boolean performFinish() {
		LocalLlamaPreferenceStore.setSetupComplete(true);
		LocalLlamaPreferenceStore.save();
		return true;
	}

	public static void open() {
		SetupWizard wizard = new SetupWizard();
		WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
		dialog.open();
	}
}
