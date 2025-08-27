package com.localllama.plugin.ui;

import java.time.LocalTime;

public class ChatMessage {

    private String message;
    private final SenderType sender;
    private final LocalTime timestamp;

    public ChatMessage(String message, SenderType sender) {
        this.message = message;
        this.sender = sender;
        this.timestamp = LocalTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SenderType getSender() {
        return sender;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public enum SenderType {
        USER,
        BOT
    }
}
