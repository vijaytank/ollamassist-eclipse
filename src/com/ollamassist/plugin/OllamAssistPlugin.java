package com.ollamassist.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class OllamAssistPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.ollamassist.plugin";
    private static OllamAssistPlugin plugin;

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

    public static OllamAssistPlugin getDefault() {
        return plugin;
    }
}
