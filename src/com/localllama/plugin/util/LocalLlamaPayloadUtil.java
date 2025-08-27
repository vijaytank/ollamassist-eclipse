package com.localllama.plugin.util;

import org.json.JSONObject;

public class LocalLlamaPayloadUtil {

	public static String createPayload(String prompt, String model) {
		JSONObject payload = new JSONObject();
		payload.put("prompt", prompt);
		payload.put("model", model);
		return payload.toString();
	}
}
