package com.localllama.plugin.rag;

import com.localllama.plugin.util.Logger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

public class JarIndexer {

    public static void indexJar(JarFile jarFile, IndexWriter writer) {
        Logger.log("Indexing JAR: " + jarFile.getName());
        try {
            for (JarEntry entry : java.util.Collections.list(jarFile.entries())) {
                if (!entry.isDirectory() && entry.getName().endsWith(".java")) {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        String content = readInputStream(is);
                        Document doc = new Document();
                        doc.add(new StringField("jar_entry", entry.getName(), Field.Store.YES));
                        doc.add(new TextField("content", content, Field.Store.YES));
                        writer.addDocument(doc);
                    } catch (Exception e) {
                        Logger.error("Error indexing JAR entry " + entry.getName(), e);
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Error indexing JAR file " + jarFile.getName(), e);
        }
    }

    private static String readInputStream(InputStream is) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
