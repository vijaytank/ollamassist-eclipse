package com.ollamassist.plugin.util;

import org.eclipse.swt.widgets.Display;

import com.ollamassist.plugin.service.OllamaClient;

public class OllamaQueryUtil {

	public static void asyncQuery(String prompt, String model, java.util.function.Consumer<String> callback) {
		Display.getDefault().asyncExec(() -> {
			String response = OllamaClient.queryModel(prompt);
			callback.accept(response);
		});
	}

	public static String blockingQuery(String prompt, String model) {
		return OllamaClient.queryModel(prompt);
	}

}
