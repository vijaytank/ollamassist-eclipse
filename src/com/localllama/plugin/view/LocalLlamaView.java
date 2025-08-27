package com.localllama.plugin.view;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.ui.ChatMessage;
import com.localllama.plugin.ui.ChatMessage.SenderType;
import com.localllama.plugin.util.EclipseEditorUtil;
import com.localllama.plugin.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalLlamaView extends ViewPart {
    public static final String ID = "com.localllama.plugin.view.LocalLlamaView";

    private Text inputText;
    private Button sendButton;
    private ScrolledComposite scrolledComposite;
    private Composite chatHistory;
    private LocalResourceManager resourceManager;
    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public void createPartControl(Composite parent) {
        this.resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);
        parent.setLayout(new GridLayout(1, false));

        createChatHistory(parent);
        createInputArea(parent);
    }

    private void createChatHistory(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        chatHistory = new Composite(scrolledComposite, SWT.NONE);
        chatHistory.setLayout(new GridLayout(1, false));
        chatHistory.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        scrolledComposite.setContent(chatHistory);
    }

    private void createInputArea(Composite parent) {
        Composite inputComposite = new Composite(parent, SWT.NONE);
        inputComposite.setLayout(new GridLayout(2, false));
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        inputText = new Text(inputComposite, SWT.BORDER | SWT.SINGLE);
        inputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        inputText.setMessage("Ask LocalLlama something...");
        inputText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.LF) {
                    if ((e.stateMask & SWT.SHIFT) == 0) {
                        e.doit = false;
                        sendMessage();
                    }
                }
            }
        });

        sendButton = new Button(inputComposite, SWT.PUSH);
        Image sendIcon = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);
        sendButton.setImage(sendIcon);
        sendButton.setToolTipText("Send");
        sendButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        sendButton.addListener(SWT.Selection, e -> sendMessage());
    }

    private void sendMessage() {
        String messageText = inputText.getText().trim();
        if (messageText.isEmpty()) {
            return;
        }

        String context = getContext(messageText);
        String augmentedMessage = context.isEmpty() ? messageText : context + "\n\n" + messageText;

        addMessage(new ChatMessage(messageText, SenderType.USER));
        inputText.setText("");

        StyledText botMessageText = addMessage(new ChatMessage("", SenderType.BOT));

        List<ChatMessage> queryMessages = new ArrayList<>(messages);
        queryMessages.get(queryMessages.size() - 2).setMessage(augmentedMessage);

        Logger.log("Sending message to LocalLlama...");

        String model = LocalLlamaPreferenceStore.getModel();
        LocalLlamaClient.streamingQuery(queryMessages, model,
            (chunk) -> {
                Display.getDefault().asyncExec(() -> {
                    if (botMessageText != null && !botMessageText.isDisposed()) {
                        botMessageText.append(chunk);
                        scrollToBottom();
                    }
                });
            },
            () -> {
                Display.getDefault().asyncExec(() -> {
                    sendButton.setEnabled(true);
                    inputText.setEnabled(true);
                    Logger.log("Finished receiving response from LocalLlama");
                });
            }
        );
        sendButton.setEnabled(false);
        inputText.setEnabled(false);
    }

    private String getContext(String messageText) {
        String editorContent = EclipseEditorUtil.getActiveEditorContent();
        String fileContent = getFileContentFromMessage(messageText);

        StringBuilder context = new StringBuilder();
        if (editorContent != null && !editorContent.isEmpty()) {
            context.append("Active editor content:\n").append(editorContent);
        }
        if (fileContent != null && !fileContent.isEmpty()) {
            if (context.length() > 0) {
                context.append("\n\n");
            }
            context.append("File content:\n").append(fileContent);
        }
        return context.toString();
    }

    private String getFileContentFromMessage(String messageText) {
        Pattern pattern = Pattern.compile("'([^'\s]+/[^'\s]+)'");
        Matcher matcher = pattern.matcher(messageText);
        if (matcher.find()) {
            String filePath = matcher.group(1);
            try {
                return new String(Files.readAllBytes(Paths.get(filePath)));
            } catch (IOException e) {
                Logger.error("Could not read file: " + filePath, e);
            }
        }
        return null;
    }

    private StyledText addMessage(ChatMessage message) {
        messages.add(message);
        StyledText messageText = renderMessage(message);
        scrollToBottom();
        return messageText;
    }

    private StyledText renderMessage(ChatMessage chatMessage) {
        Composite messageComposite = new Composite(chatHistory, SWT.NONE);
        messageComposite.setLayout(new GridLayout(2, false));
        messageComposite.setBackground(chatHistory.getBackground());
        messageComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        Label iconLabel = new Label(messageComposite, SWT.NONE);
        iconLabel.setImage(getIcon(chatMessage.getSender()));
        iconLabel.setBackground(chatHistory.getBackground());

        StyledText messageText = new StyledText(messageComposite, SWT.WRAP | SWT.READ_ONLY);
        messageText.setText(chatMessage.getMessage());
        messageText.setFont(getFont(chatMessage.getSender()));
        messageText.setBackground(chatHistory.getBackground());
        messageText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return messageText;
    }

    private void scrollToBottom() {
        chatHistory.layout(true, true);
        scrolledComposite.setMinSize(chatHistory.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scrolledComposite.setOrigin(0, chatHistory.getSize().y);
    }

    private Image getIcon(SenderType sender) {
        String imageKey = sender == SenderType.USER ? ISharedImages.IMG_OBJ_ELEMENT : ISharedImages.IMG_TOOL_FORWARD;
        return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
    }

    private Font getFont(SenderType sender) {
        org.eclipse.swt.graphics.FontData[] fontData = JFaceResources.getDefaultFont().getFontData();
        int style = sender == SenderType.USER ? SWT.BOLD : SWT.NORMAL;
        FontDescriptor descriptor = FontDescriptor.createFrom(fontData[0].getName(), fontData[0].getHeight(), style);
        return resourceManager.createFont(descriptor);
    }

    @Override
    public void setFocus() {
        inputText.setFocus();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
