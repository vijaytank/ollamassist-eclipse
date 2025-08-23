package com.ollamassist.plugin.util;

import com.ollamassist.plugin.service.OllamaClient;

public class CommitMessageGenerator {
	public static String generateMessage(String diff) {
		String prompt = "Generate a concise commit message for this diff:\n" + diff;
		return OllamaClient.queryModel(prompt);
	}
}
