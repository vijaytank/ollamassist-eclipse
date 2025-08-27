package com.localllama.plugin.util;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;

public class Logger {

    public static void log(String message) {
        if (LocalLlamaPreferenceStore.isLoggingEnabled()) {
            System.out.println("[LocalLlama] " + message);
        }
    }

    public static void error(String message, Throwable t) {
        if (LocalLlamaPreferenceStore.isLoggingEnabled()) {
            System.err.println("[LocalLlama] Error: " + message);
            t.printStackTrace();
        }
    }
}
