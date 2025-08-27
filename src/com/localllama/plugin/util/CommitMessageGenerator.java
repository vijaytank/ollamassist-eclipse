package com.localllama.plugin.util;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;

public class CommitMessageGenerator {
	public static String generateMessage(String diff) {
		if (diff == null || diff.trim().isEmpty()) {
			return "No changes to commit.";
		}
		// Retrieve custom prompt from preferences
		String customPrompt = LocalLlamaPreferenceStore.getCommitMessagePrompt();
		String prompt;

		// Use custom prompt if available, otherwise use the default
		if (customPrompt != null && !customPrompt.trim().isEmpty()) {
			prompt = customPrompt + "\n\n" + diff;
		} else {
			// Default prompt for Conventional Commits
			prompt = LocalLlamaPreferenceStore.getCommitMessagePrompt() + "\n\n" + diff;
		}
		String model = LocalLlamaPreferenceStore.getModel();
		return LocalLlamaQueryUtil.blockingQuery(prompt, model);
	}

	public static String generateFromRepo(String repoPath) {
		String diff = GitDiffUtil.getDiff(repoPath);
		return generateMessage(diff);
	}
}
