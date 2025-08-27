package com.localllama.plugin.util;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;

public class CommitMessageGenerator {
	public static String generateMessage(String diff) {
		// Retrieve custom prompt from preferences
		String customPrompt = LocalLlamaPreferenceStore.getCommitMessagePrompt();
		String prompt;

		// Use custom prompt if available, otherwise use the default
		if (customPrompt != null && !customPrompt.trim().isEmpty()) {
			prompt = customPrompt + "\n\n" + diff;
		} else {
			// Default prompt for Conventional Commits
			prompt = "Generate a concise Conventional Commit message for the following Git diff. The message should follow the format: type(scope): description\n\n[optional body]\n\n[optional footer].\n\n"
					+ diff;
		}
		String model = LocalLlamaPreferenceStore.getModel();
		return LocalLlamaQueryUtil.blockingQuery(prompt, model);
	}

	public static String generateFromRepo(String repoPath) {
		String diff = GitDiffUtil.getDiff(repoPath);
		return generateMessage(diff);
	}
}
