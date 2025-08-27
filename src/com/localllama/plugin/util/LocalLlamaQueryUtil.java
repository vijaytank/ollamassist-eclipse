package com.localllama.plugin.util;

import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.ui.ChatMessage;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.swt.widgets.Display;

public class LocalLlamaQueryUtil {

    public static void asyncQuery(String prompt, String model, Consumer<String> callback) {
        StringBuffer responseBuilder = new StringBuffer();
        List<ChatMessage> messages = Collections.singletonList(new ChatMessage(ChatMessage.SenderType.USER, prompt));

        Runnable doneCallback = () -> {
            Display.getDefault().asyncExec(() -> {
                callback.accept(responseBuilder.toString());
            });
        };

        Consumer<String> chunkCallback = responseBuilder::append;

        Consumer<String> errorCallback = error -> {
            Logger.error("An error occurred during async query: " + error, null);
            // Optionally, you can display the error to the user in the UI
        };

        LocalLlamaClient.streamingQuery(messages, model, chunkCallback, doneCallback, errorCallback);
    }

    public static String blockingQuery(String prompt, String model) {
        return LocalLlamaClient.generateCompletion(prompt, model);
    }
}
