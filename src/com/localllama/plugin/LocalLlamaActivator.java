package com.localllama.plugin;

import com.localllama.plugin.rag.ActiveFileIndexer;
import com.localllama.plugin.rag.ProjectDependencyTracker;
import com.localllama.plugin.util.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class LocalLlamaActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "com.localllama.plugin";
	private static LocalLlamaActivator plugin;
	private Path indexPath;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ActiveFileIndexer.shutdown();
		ProjectDependencyTracker.closeAll();
		plugin = null;
		super.stop(context);
	}

	public static LocalLlamaActivator getDefault() {
		return plugin;
	}

	private Path getStateLocationPath() {
		try {
			IPath stateLocation = getStateLocation();
			if (stateLocation != null) {
				return stateLocation.toPath();
			}
		} catch (IllegalStateException e) {
			// This can happen if the plugin is not running, for example in a test environment.
			Logger.error("Failed to get state location", e);
		}
		// Fallback to a temporary directory
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "localllama_state");
		try {
			Files.createDirectories(tempDir);
		} catch (IOException ex) {
			Logger.error("Failed to create temporary directory", ex);
		}
		return tempDir;
	}

	public Path getIndexPath() {
		if (indexPath == null) {
			this.indexPath = getStateLocationPath().resolve("index");
			try {
				Files.createDirectories(this.indexPath);
			} catch (IOException e) {
				Logger.error("Failed to create index directory", e);
			}
		}
		return indexPath;
	}
}
