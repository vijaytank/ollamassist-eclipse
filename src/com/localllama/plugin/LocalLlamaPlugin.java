package com.localllama.plugin;

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

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import com.localllama.plugin.service.LocalLlamaClient;

public class LocalLlamaPlugin {

	private static LocalLlamaPlugin instance;
	private final Path indexPath;

	private LocalLlamaPlugin() {
		indexPath = Paths.get(System.getProperty("user.home"), ".localllama", "index");
		try {
			Files.createDirectories(indexPath);
		} catch (IOException e) {
			e.printStackTrace(); // Consider logging in production
		}
	}

	public static synchronized LocalLlamaPlugin getInstance() {
		if (instance == null) {
			instance = new LocalLlamaPlugin();
		}
		return instance;
	}

	public String sendMessage(String input) {
		String model = LocalLlamaPreferenceStore.getModel();
        return LocalLlamaClient.blockingQuery(input, model);
	}

	private void indexInput(String input) {
		try (FSDirectory dir = FSDirectory.open(indexPath);
				IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()))) {

			Document doc = new Document();
			doc.add(new StringField("id", String.valueOf(System.currentTimeMillis()), Field.Store.YES));
			doc.add(new TextField("content", input, Field.Store.YES));
			writer.addDocument(doc);

		} catch (IOException e) {
			e.printStackTrace(); // Consider logging in production
		}
	}
}
