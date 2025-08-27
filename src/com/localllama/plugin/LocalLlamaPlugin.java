package com.localllama.plugin;

import com.localllama.plugin.preferences.LocalLlamaPreferenceStore;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

public class LocalLlamaPlugin {

    private static LocalLlamaPlugin instance;
    private final Path indexPath;

    private LocalLlamaPlugin() {
        this.indexPath = getStateLocation().append("index");
    }

    private File getStateLocation() {
        try {
            return Activator.getDefault().getStateLocation().toFile();
        } catch (IllegalStateException e) {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "localllama_state");
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            return tempDir;
        }
    }

    public static synchronized LocalLlamaPlugin getInstance() {
        if (instance == null) {
            instance = new LocalLlamaPlugin();
        }
        return instance;
    }

    private void indexInput(String input) {
        try (FSDirectory dir = FSDirectory.open(indexPath);
             IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()))) {

            Document doc = new Document();
            doc.add(new StringField("id", String.valueOf(System.currentTimeMillis()), Field.Store.YES));
            doc.add(new TextField("content", input, Field.Store.YES));
            writer.addDocument(doc);

        } catch (IOException e) {
            // Error handling for production would go here
        }    }
}
