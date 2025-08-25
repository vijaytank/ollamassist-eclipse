package com.ollamassist.plugin.setup;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.util.WizardUIUtil;

public class ModelSelectionPage extends WizardPage {

	private Combo modelCombo;

	protected ModelSelectionPage() {
		super("Model Selection");
		setTitle("Select Local Model");
		setDescription("Choose the local Llama model to use for code assistance.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = WizardUIUtil.createContainer(parent);

		Label label = new Label(container, SWT.NONE);
		label.setText("Available Models:");

		modelCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		modelCombo.setItems(com.localllama.plugin.util.ModelSelectorUtil.getModelNames());
		modelCombo.select(0);

		modelCombo.addListener(SWT.Selection, e -> {
			LocalLlamaPreferenceStore.setModel(modelCombo.getText());
		});

		// Initial set
		LocalLlamaPreferenceStore.setModel(modelCombo.getText());

		setControl(container);
	}

	@Override
	public boolean isPageComplete() {
		return modelCombo.getSelectionIndex() >= 0;
	}
}
