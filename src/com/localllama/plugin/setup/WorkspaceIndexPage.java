package com.localllama.plugin.setup;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.rag.ProjectDependencyTracker;
import com.localllama.plugin.util.WizardUIUtil;

public class WorkspaceIndexPage extends WizardPage {
	private Button indexButton;
	private Label statusLabel;

	protected WorkspaceIndexPage() {
		super("Workspace Indexing");
		setTitle("Index Workspace");
		setDescription("Initialize symbol tracking and file dependencies.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = WizardUIUtil.createContainer(parent);
		indexButton = new Button(container, SWT.PUSH);
		indexButton.setText("Index Workspace Now");
		statusLabel = new Label(container, SWT.NONE);
		statusLabel.setText("Status: Not indexed");

		indexButton.addListener(SWT.Selection, e -> {
			ProjectDependencyTracker.indexAllOpenProjects();
			statusLabel.setText("Status: Indexing complete");
			LocalLlamaPreferenceStore.setWorkspaceIndexed(true);
			setPageComplete(true);
		});

		setControl(container);
		setPageComplete(false);
	}
}
