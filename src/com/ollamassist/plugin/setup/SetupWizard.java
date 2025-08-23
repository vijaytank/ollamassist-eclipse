package com.ollamassist.plugin.setup;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

public class SetupWizard extends Wizard {

	public SetupWizard() {
		setWindowTitle("OllamAssist Setup");
	}

	@Override
	public void addPages() {
		addPage(new ModelSelectionPage());
		addPage(new WorkspaceIndexPage());
		addPage(new FinalConfirmationPage());
	}

	@Override
	public boolean performFinish() {
		SetupConfig.save();
		return true;
	}

	public static void open() {
		SetupWizard wizard = new SetupWizard();
		WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
		dialog.open();
	}
}
