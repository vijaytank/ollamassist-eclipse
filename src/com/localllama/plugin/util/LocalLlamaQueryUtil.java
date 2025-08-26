package com.localllama.plugin.util;

import org.eclipse.swt.widgets.Display;

import com.localllama.plugin.service.LocalLlamaClient;

public class LocalLlamaQueryUtil {

	public static void asyncQuery(String prompt, String model, java.util.function.Consumer<String> callback) {
		Display.getDefault().asyncExec(() -> { // This method is already using the correct signature
			String response = LocalLlamaClient.queryModel(prompt, model); // Ensure this calls the correct overloaded
																			// method
			callback.accept(response);
		});
	}

	public static String blockingQuery(String prompt, String model) { // This method is already using the correct
																		// signature
		return LocalLlamaClient.queryModel(prompt, model); // Ensure this calls the correct overloaded method
	}

}