package com.localllama.plugin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitDiffUtil {
	public static String getDiff(String repoPath) {
		try {
			ProcessBuilder pb = new ProcessBuilder("git", "-C", repoPath, "diff");
			Process process = pb.start();
			StringBuilder diff = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					diff.append(line).append("\n");
				}
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					Logger.error("Error from git diff: " + line, null);
				}
			}
			return diff.toString();
		} catch (IOException e) {
			Logger.error("Error fetching git diff", e);
			return "Error fetching git diff: " + e.getMessage();
		}
	}
}
