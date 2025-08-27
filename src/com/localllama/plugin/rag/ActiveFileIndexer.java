package com.localllama.plugin.rag;

import com.localllama.plugin.util.Logger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ActiveFileIndexer {

    public static void indexActiveFile() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
                IEditorPart editor = page.getActiveEditor();
                if (editor != null) {
                    IFile file = editor.getEditorInput().getAdapter(IFile.class);
                    if (file != null) {
                        IProject project = file.getProject();
                        new Thread(() -> {
                            try {
                                IndexWriter writer = ProjectDependencyTracker.getIndexWriter(project);
                                indexFile(file, writer);
                                writer.commit();
                                Logger.log("Re-indexed active file: " + file.getName());
                            } catch (Exception e) {
                                Logger.error("Failed to re-index active file", e);
                            }
                        }).start();
                    }
                }
            }
        }
    }

    private static void indexFile(IFile file, IndexWriter writer) {
        try {
            String content = readFile(file);
            Document doc = new Document();
            doc.add(new StringField("filename", file.getName(), Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
        } catch (Exception e) {
            Logger.error("Failed to index file: " + file.getName(), e);
        }
    }

    private static String readFile(IFile file) {
        try (InputStream is = file.getContents();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            Logger.error("Error reading file " + file.getName(), e);
            return "";
        }
    }
}
