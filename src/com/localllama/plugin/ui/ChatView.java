package com.localllama.plugin.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.localllama.plugin.rag.ProjectDependencyTracker;
import com.localllama.plugin.util.LocalLlamaQueryUtil;
import com.localllama.plugin.util.ModelSelectorUtil;

public class ChatView extends ViewPart {

	public static final String ID = "com.localllama.plugin.ui.ChatView";

	private StyledText chatHistory;
	private Text inputField;
	private Combo modelSelector;

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		new ArrayList<>();

		modelSelector = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		modelSelector.setItems(ModelSelectorUtil.getModelNames());
		modelSelector.select(0);
		ModelSelectorUtil.setSelectedModel(modelSelector.getText());
		modelSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		chatHistory = new StyledText(container, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		chatHistory.setEditable(false);
		chatHistory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		inputField = new Text(container, SWT.BORDER);
		inputField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		inputField.setMessage("Type your message and press Enter...");

		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					String userInput = inputField.getText().trim();
					if (!userInput.isEmpty()) {
						appendMessage("You", userInput);
						queryOllama(userInput);
					}
				}
			}
		});
	}

	private void appendMessage(String sender, String message) {
		Display.getDefault().asyncExec(() -> {
			if (!chatHistory.isDisposed()) {
				chatHistory.append("\n" + sender + ": ");
				chatHistory.append(message);
				chatHistory.append("\n");
			}
		});
	}

	private String getActiveFileContext() {
		try {
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editor instanceof ITextEditor && editor.getEditorInput() instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
				String content = ProjectDependencyTracker.getFileContent(file.getName());
				return "// Active File: " + file.getName() + "\n" + content + "\n\n";
			}
		} catch (Exception e) {
			// Silent fail
		}
		return "";
	}

	private void queryOllama(String prompt) {
		String model = modelSelector.getText();
		String context = getActiveFileContext();
		String fullPrompt = context + prompt;
		LocalLlamaQueryUtil.asyncQuery(fullPrompt, model, response -> {
			appendMessage("Ollam (" + model + ")", response);
			appendMessage("LocalLlama (" + model + ")", response);
		});
	}

	@Override
	public void setFocus() {
		inputField.setFocus();
	}
}
