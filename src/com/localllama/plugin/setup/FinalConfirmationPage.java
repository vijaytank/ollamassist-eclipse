package com.localllama.plugin.setup;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.util.WizardUIUtil;

public class FinalConfirmationPage extends WizardPage {
	private Label summary;

	protected FinalConfirmationPage() {
		super("Confirmation");
		setTitle("Setup Complete");
		setDescription("Review your LocalLlama configuration and finish setup.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = WizardUIUtil.createContainer(parent);
		summary = new Label(container, SWT.WRAP);
		summary.setText(""); // Will be populated in setVisible()
		Label note = new Label(container, SWT.WRAP);
		note.setText("\nYou can change these settings later in Preferences.");
		setControl(container);
		setPageComplete(true);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && summary != null) {
			String model = LocalLlamaPreferenceStore.getModel();
			boolean indexed = LocalLlamaPreferenceStore.isWorkspaceIndexed();
			summary.setText("✔ Model: " + (model != null && !model.isEmpty() ? model : "Not set")
					+ "\n✔ Workspace Indexed: " + (indexed ? "Yes" : "No"));
		}
	}
}
