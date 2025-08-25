package com.localllama.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class LocalLlamaPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.localllama.plugin";
    private static LocalLlamaPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static LocalLlamaPlugin getDefault() {
        return plugin;
    }
}