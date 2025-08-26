package com.localllama.plugin.util;

import java.util.HashSet; // Keep existing imports
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymbolExtractor {

	private static final Pattern CLASS_PATTERN = Pattern.compile("\\bclass\\s+(\\w+)");
	private static final Pattern METHOD_PATTERN = Pattern
			.compile("\\b(?:public|private|protected)?\\s+\\w+\\s+(\\w+)\\s*\\(");
	private static final Pattern PROMPT_PATTERN = Pattern.compile("\\b([A-Z][a-zA-Z0-9_]*)\\b");

	public static Set<String> extractFromCode(String content) {
		Set<String> symbols = new HashSet<>();

		Matcher classMatcher = CLASS_PATTERN.matcher(content);
		while (classMatcher.find()) {
			symbols.add(classMatcher.group(1));
		}

		Matcher methodMatcher = METHOD_PATTERN.matcher(content);
		while (methodMatcher.find()) {
			symbols.add(methodMatcher.group(1));
		}

		return symbols;
	}

	public static Set<String> extractFromPrompt(String prompt) {
		Set<String> symbols = new HashSet<>();
		Matcher matcher = PROMPT_PATTERN.matcher(prompt);
		while (matcher.find()) {
			symbols.add(matcher.group(1));
		}
		return symbols;
	}
}
