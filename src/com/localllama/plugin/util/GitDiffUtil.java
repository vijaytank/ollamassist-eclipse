package com.localllama.plugin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitDiffUtil {
	public static String getDiff(String repoPath) {
		try {
			ProcessBuilder pb = new ProcessBuilder("git", "-C", repoPath, "diff");
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder diff = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				diff.append(line).append("\n");
			}
			return diff.toString();
		} catch (IOException e) {
			return "Error fetching git diff: " + e.getMessage();
		}
	}
}
