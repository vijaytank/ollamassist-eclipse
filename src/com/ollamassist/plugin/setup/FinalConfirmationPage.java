package com.ollamassist.plugin.setup;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FinalConfirmationPage extends WizardPage {

	private Label summary;

	protected FinalConfirmationPage() {
		super("Confirmation");
		setTitle("Setup Complete");
		setDescription("Review your configuration and finish setup.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		summary = new Label(container, SWT.WRAP);
		summary.setText(""); // Will be populated in setVisible()

		Label note = new Label(container, SWT.WRAP);
		note.setText("\nYou can change these settings later in \nPreferences.");

		setControl(container);
		setPageComplete(true);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && summary != null) {
			String model = SetupConfig.get("model");
			String indexed = SetupConfig.get("workspaceIndexed");
			summary.setText("✔ Model: " + (model != null ? model : "Not set") + "\n✔ Workspace Indexed: "
					+ (indexed != null ? indexed : "Not set"));
		}
	}
}
