package com.localllama.plugin.ui;

public class ChatMessage {
	private String role;
	private String content;

	public ChatMessage(String role, String content) {
		this.role = role;
		this.content = content;
	}

	public String getRole() {
		return role;
	}

	public String getContent() {
		return content;
	}
}