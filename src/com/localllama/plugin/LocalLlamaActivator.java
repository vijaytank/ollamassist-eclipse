package com.localllama.plugin;

import com.localllama.plugin.rag.ActiveFileIndexer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
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
		}
		// Fallback to a temporary directory
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "localllama_state");
		try {
			Files.createDirectories(tempDir);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tempDir;
	}

	public Path getIndexPath() {
		if (indexPath == null) {
			this.indexPath = getStateLocationPath().resolve("index");
			try {
				Files.createDirectories(this.indexPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return indexPath;
	}

	public void indexInput(String input) {
		try (FSDirectory dir = FSDirectory.open(getIndexPath());
			 IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()))) {

			Document doc = new Document();
			doc.add(new StringField("id", String.valueOf(System.currentTimeMillis()), Field.Store.YES));
			doc.add(new TextField("content", input, Field.Store.YES));
			writer.addDocument(doc);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
