package com.localllama.plugin.rag;

import com.localllama.plugin.util.IndexerUtil;
import com.localllama.plugin.util.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.lucene.index.IndexWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ActiveFileIndexer {

    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void indexFile(IFile file) {
        if (file == null) {
            return;
        }
        IProject project = file.getProject();
        executor.submit(() -> {
            try {
                IndexWriter writer = ProjectDependencyTracker.getIndexWriter(project);
                if (writer != null) {
                    IndexerUtil.indexFile(file, writer);
                    Logger.log("Re-indexed file: " + file.getName());
                }
            } catch (Exception e) {
                Logger.error("Failed to re-index file", e);
            }
        });
    }

    public static void indexActiveFile() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
                IEditorPart editor = page.getActiveEditor();
                if (editor != null) {
                    IFile file = editor.getEditorInput().getAdapter(IFile.class);
                    indexFile(file);
                }
            }
        }
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
