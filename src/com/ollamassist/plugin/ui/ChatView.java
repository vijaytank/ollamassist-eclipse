package com.ollamassist.plugin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

import com.ollamassist.plugin.service.OllamaClient;

public class ChatView extends ViewPart {

    public static final String ID = "com.ollamassist.plugin.ui.ChatView";

    private StyledText chatHistory;
    private Text inputField;
    private Combo modelSelector;

    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        // 🔹 Model Selector
        modelSelector = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        modelSelector.setItems(new String[] { "llama3.1", "mistral", "custom" });
        modelSelector.select(0);
        modelSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // 🔹 Chat History
        chatHistory = new StyledText(container, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        chatHistory.setEditable(false);
        chatHistory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // 🔹 Input Field
        inputField = new Text(container, SWT.BORDER);
        inputField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        inputField.setMessage("Type your message and press Enter...");

        // 🔹 Enter-to-send
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    String userInput = inputField.getText().trim();
                    if (!userInput.isEmpty()) {
                        appendMessage("You", userInput);
                        inputField.setText("");
                        queryOllama(userInput);
                    }
                }
            }
        });
    }

    private void appendMessage(String sender, String message) {
        chatHistory.append(sender + ": " + message + "\n");
    }

    private void queryOllama(String prompt) {
        Display.getDefault().asyncExec(() -> {
            String model = modelSelector.getText();
            String response = OllamaClient.queryModel(prompt);
            appendMessage("Ollam (" + model + ")", response);
        });
    }

    @Override
    public void setFocus() {
        inputField.setFocus();
    }
}
