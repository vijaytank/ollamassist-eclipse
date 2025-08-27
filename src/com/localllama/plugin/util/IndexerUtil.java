package com.localllama.plugin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class IndexerUtil {

    public static void indexFile(IFile file, IndexWriter writer) {
        try {
            String content = readFile(file);
            Document doc = new Document();
            doc.add(new StringField("path", file.getFullPath().toString(), Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
        } catch (IOException | CoreException e) {
            Logger.error("Failed to index file: " + file.getName(), e);
        }
    }

    public static String readFile(IFile file) throws CoreException, IOException {
        try (InputStream is = file.getContents()) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[4096];
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, n);
                }
            }
            return sb.toString();
        }
    }
}
