package com.localllama.plugin.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.localllama.plugin.util.LocalLlamaQueryUtil;

public class LocalLlamaView extends ViewPart {
	public static final String ID = "com.localllama.plugin.view.LocalLlamaView";

	private Text inputText;
	private Text outputText;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		inputText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		inputText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		inputText.setMessage("Ask LocalLlama something...");

		Button queryButton = new Button(parent, SWT.PUSH);
		queryButton.setText("Query LocalLlama");

		outputText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
		outputText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		queryButton.addListener(SWT.Selection, e -> {
			String prompt = inputText.getText().trim();
			if (!prompt.isEmpty()) {
				outputText.setText("Querying LocalLlama...");
				LocalLlamaQueryUtil.asyncQuery(prompt, null, response -> {
					outputText.setText(response);
				});

			}
		});
	}

	@Override
	public void setFocus() {
		inputText.setFocus();
	}
}