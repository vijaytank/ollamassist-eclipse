package com.localllama.plugin.util;

import com.localllama.plugin.LocalLlamaActivator;
import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Logger {

    private static final ILog log = LocalLlamaActivator.getDefault().getLog();

    public static void log(String message) {
        if (LocalLlamaPreferenceStore.isLoggingEnabled()) {
            log.log(new Status(IStatus.INFO, LocalLlamaActivator.PLUGIN_ID, message));
        }
    }

    public static void error(String message, Throwable t) {
        if (LocalLlamaPreferenceStore.isLoggingEnabled()) {
            log.log(new Status(IStatus.ERROR, LocalLlamaActivator.PLUGIN_ID, message, t));
        }
    }

    public static void error(Throwable t) {
        if (LocalLlamaPreferenceStore.isLoggingEnabled()) {
            log.log(new Status(IStatus.ERROR, LocalLlamaActivator.PLUGIN_ID, t.getMessage(), t));
        }
    }
}
