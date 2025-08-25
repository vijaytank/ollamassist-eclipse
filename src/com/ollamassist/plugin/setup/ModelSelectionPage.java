package com.ollamassist.plugin.setup;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ollamassist.plugin.util.ModelSelectorUtil;
import com.ollamassist.plugin.util.WizardUIUtil;

public class ModelSelectionPage extends WizardPage {

	private Combo modelCombo;

	protected ModelSelectionPage() {
		super("Model Selection");
		setTitle("Select Local Model");
		setDescription("Choose the local Ollama model to use for code assistance.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = WizardUIUtil.createContainer(parent);

		Label label = new Label(container, SWT.NONE);
		label.setText("Available Models:");

		modelCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		modelCombo.setItems(ModelSelectorUtil.getModelNames());
		modelCombo.select(0);
		ModelSelectorUtil.setSelectedModel(modelCombo.getText());

		modelCombo.addListener(SWT.Selection, e -> {
			SetupConfig.set("model", modelCombo.getText());
		});

		// Initial set
		SetupConfig.set("model", modelCombo.getText());

		setControl(container);
	}

	@Override
	public boolean isPageComplete() {
		return modelCombo.getSelectionIndex() >= 0;
	}
}
