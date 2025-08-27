package com.localllama.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.localllama.plugin.rag.ActiveFileIndexer;
import com.localllama.plugin.rag.ProjectDependencyTracker;
import com.localllama.plugin.rag.ResourceChangeListener;
import com.localllama.plugin.util.Logger;

public class LocalLlamaActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "com.localllama.plugin";
	private static LocalLlamaActivator plugin;
	private Path indexPath;
	private IResourceChangeListener resourceChangeListener;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// ✅ Register resource change listener
		resourceChangeListener = new ResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener,
				IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// ✅ Unregister listener
		if (resourceChangeListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
			resourceChangeListener = null;
		}
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
			Logger.error("Failed to get state location", e);
		}
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
