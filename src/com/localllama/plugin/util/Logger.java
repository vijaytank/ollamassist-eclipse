package com.localllama.plugin.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.localllama.plugin.LocalLlamaActivator;
import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;

public class Logger {

	private static final ILog log = LocalLlamaActivator.getDefault().getLog();
	private static boolean loggingEnabled = LocalLlamaPreferenceStore.isLoggingEnabled();

	public static void log(String message) {
		if (loggingEnabled) {
			log.log(new Status(IStatus.INFO, LocalLlamaActivator.PLUGIN_ID, message));
		}
	}

	public static void error(String message, Throwable t) {
		if (loggingEnabled) {
			log.log(new Status(IStatus.ERROR, LocalLlamaActivator.PLUGIN_ID, message, t));
		}
	}

	public static void error(Throwable t) {
		if (loggingEnabled) {
			log.log(new Status(IStatus.ERROR, LocalLlamaActivator.PLUGIN_ID, t.getMessage(), t));
		}
	}

	public static void updateLoggingPreference() {
		loggingEnabled = LocalLlamaPreferenceStore.isLoggingEnabled();
	}
}
