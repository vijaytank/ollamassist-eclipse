package com.localllama.plugin.util;

public class LocalLlamaJsonUtil {
	public static String escapeJson(String text) {
		return text.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	public static String parseResponse(String json) {
		int start = json.indexOf("\"response\":\"");
		if (start == -1) {
			return "";
		}
		start += 11;
		int end = json.indexOf("\"", start);
		if (end == -1) {
			return "";
		}
		return json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
	}
}