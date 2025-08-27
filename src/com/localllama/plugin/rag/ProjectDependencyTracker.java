package com.localllama.plugin.rag;

import com.localllama.plugin.LocalLlamaActivator;
import com.localllama.plugin.util.IndexerUtil;
import com.localllama.plugin.util.Logger;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class ProjectDependencyTracker {

    private static final Map<IProject, Boolean> indexedProjects = new HashMap<>();
    private static final Map<IProject, IndexWriter> indexWriters = new HashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void indexAllOpenProjects() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.isOpen() && !isProjectIndexed(project)) {
                executor.submit(() -> indexProject(project));
            }
        }
    }

    public static void indexProject(IProject project) {
        Logger.log("Indexing project: " + project.getName());
        try {
            IndexWriter writer = getIndexWriter(project);
            project.accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource resource) throws CoreException {
                    if (resource instanceof IFile) {
                        IFile file = (IFile) resource;
                        if ("java".equals(file.getFileExtension())) {
                            IndexerUtil.indexFile(file, writer);
                        } else if ("jar".equals(file.getFileExtension())) {
                            indexJar(file, writer);
                        }
                    }
                    return true;
                }
            });
            writer.commit();
            indexedProjects.put(project, true);
            Logger.log("Finished indexing project: " + project.getName());
        } catch (Exception e) {
            Logger.error("Indexing failed for project " + project.getName(), e);
        }
    }

    public static synchronized IndexWriter getIndexWriter(IProject project) throws IOException {
        if (indexWriters.containsKey(project)) {
            return indexWriters.get(project);
        }
        Path indexPath = getIndexPath(project);
        FSDirectory dir = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(dir, config);
        indexWriters.put(project, writer);
        return writer;
    }

    private static void indexJar(IFile file, IndexWriter writer) {
        try (JarFile jarFile = new JarFile(file.getLocation().toFile())) {
            JarIndexer.indexJar(jarFile, writer);
        } catch (IOException e) {
            Logger.error("Failed to index JAR file: " + file.getName(), e);
        }
    }

    public static boolean isProjectIndexed(IProject project) {
        if (indexedProjects.containsKey(project)) {
            return indexedProjects.get(project);
        }
        try {
            Path indexPath = getIndexPath(project);
            if (indexPath.toFile().exists()) {
                return DirectoryReader.indexExists(FSDirectory.open(indexPath));
            }
        } catch (IOException e) {
            Logger.error("Error checking if project is indexed", e);
        }
        return false;
    }

    public static String searchContent(IProject project, String symbol) {
        Logger.log("Searching for symbol '" + symbol + "' in project " + project.getName());
        try {
            Path indexPath = getIndexPath(project);
            FSDirectory dir = FSDirectory.open(indexPath);
            try (DirectoryReader reader = DirectoryReader.open(dir)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                QueryParser parser = new QueryParser("content", new StandardAnalyzer());
                Query query = parser.parse(symbol);
                TopDocs results = searcher.search(query, 5);
                StringBuilder sb = new StringBuilder();
                for (ScoreDoc sd : results.scoreDocs) {
                    Document doc = searcher.doc(sd.doc);
                    String path = doc.get("path");
                    String jarEntry = doc.get("jar_entry");
                    if (path != null) {
                        sb.append("// Path: ").append(path);
                        if (jarEntry != null) {
                            sb.append(" Entry: ").append(jarEntry);
                        }
                        sb.append("\n");
                    }
                    sb.append(doc.get("content")).append("\n\n");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            Logger.error("Error searching for symbol '" + symbol + "'", e);
            return "";
        }
    }

    private static Path getIndexPath(IProject project) {
        return LocalLlamaActivator.getDefault().getIndexPath().resolve(project.getName());
    }

    public static void closeAll() {
        executor.shutdown();
        for (IndexWriter writer : indexWriters.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                Logger.error("Error closing IndexWriter", e);
            }
        }
        indexWriters.clear();
    }
}
