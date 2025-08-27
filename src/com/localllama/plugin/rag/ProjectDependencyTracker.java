package com.localllama.plugin.rag;

import com.localllama.plugin.util.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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

    public static void indexAllOpenProjects() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.isOpen() && !isProjectIndexed(project)) {
                new Thread(() -> indexProject(project)).start();
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
                            indexFile(file, writer);
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

    public static IndexWriter getIndexWriter(IProject project) throws IOException {
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

    private static void indexFile(IFile file, IndexWriter writer) {
        try {
            String content = readFile(file);
            Document doc = new Document();
            doc.add(new StringField("filename", file.getName(), Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
        } catch (IOException e) {
            Logger.error("Failed to index file: " + file.getName(), e);
        }
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
        Path indexPath = getIndexPath(project).resolve("segments_1");
        boolean exists = indexPath.toFile().exists();
        indexedProjects.put(project, exists);
        return exists;
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
                    String filename = doc.get("filename");
                    String jarEntry = doc.get("jar_entry");
                    if (filename != null) {
                        sb.append("// File: ").append(filename).append("\n");
                    } else if (jarEntry != null) {
                        sb.append("// JAR Entry: ").append(jarEntry).append("\n");
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
        String userHome = System.getProperty("user.home");
        String projectName = project.getName();
        return Paths.get(userHome, ".localllama", "LocalLlamaAssist", projectName, "database", "knowledge_index");
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
