package com.localllama.plugin.view;

import java.util.concurrent.CompletableFuture;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.service.LocalLlamaClient;

public class LocalLlamaView extends ViewPart {
	public static final String ID = "com.localllama.plugin.view.LocalLlamaView";

	private Text inputText;
	private Text outputText;
	private Label statusLabel;
	private Button sendButton;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		inputText = new Text(parent, SWT.BORDER);
		inputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		inputText.setMessage("Ask LocalLlama something...");
		inputText.setToolTipText("Press Enter to send");

		inputText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.LF) {
					sendMessage();
				}
			}
		});

		sendButton = new Button(parent, SWT.PUSH);
		sendButton.setText("Query LocalLlama");
		sendButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		sendButton.addListener(SWT.Selection, e -> sendMessage());

		statusLabel = new Label(parent, SWT.NONE);
		statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		statusLabel.setText("Ready");

		outputText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
		outputText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void sendMessage() {
		String input = inputText.getText().trim();
		if (!input.isEmpty()) {
			statusLabel.setText("Processing...");
			sendButton.setEnabled(false);

			CompletableFuture.supplyAsync(() -> {
				try {
					String model = LocalLlamaPreferenceStore.getModel();
					return LocalLlamaClient.blockingQuery(input, model);
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}).thenAccept(response -> Display.getDefault().asyncExec(() -> {
				if (response != null && !response.isEmpty()) {
					outputText.setText(response);
					statusLabel.setText("Done");
					inputText.setText("");
					inputText.clearSelection();
				} else {
					outputText.setText("No response received.");
					statusLabel.setText("Error");
				}
				sendButton.setEnabled(true);
			}));
		}
	}

	@Override
	public void setFocus() {
		inputText.setFocus();
	}
}
