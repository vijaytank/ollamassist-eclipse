package com.localllama.plugin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalLlamaPromptUtil {

	public static String escapeJson(String input) {
		return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
	}

	public static String parseResponse(String json) {
		Pattern pattern = Pattern.compile("\"response\":\"(.*?)\"");
		Matcher matcher = pattern.matcher(json);
		if (matcher.find()) {
			return matcher.group(1).replace("\\n", "\n");
		}
		return "";
	}
}