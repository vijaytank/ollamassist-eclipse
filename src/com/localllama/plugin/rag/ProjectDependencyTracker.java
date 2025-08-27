package com.localllama.plugin.rag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public class ProjectDependencyTracker {

	private static final Map<String, Set<String>> symbolToFiles = new HashMap<>();
	private static final Map<IFile, String> fileContents = new HashMap<>();
	private static final List<String> indexedFilePaths = new ArrayList<>();
	private static final Map<IProject, Boolean> indexedProjects = new HashMap<>();

	public static void indexAllOpenProjects() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (project.isOpen() && !isProjectIndexed(project)) {
				new Thread(() -> indexProject(project)).start();
			}
		}
	}

	public static void indexProject(IProject project) {
		try {
			Path indexPath = getIndexPath(project);
			FSDirectory dir = FSDirectory.open(indexPath);
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			try (IndexWriter writer = new IndexWriter(dir, config)) {
				project.accept(resource -> {
					if (resource instanceof IFile file && file.getFileExtension() != null
							&& (file.getFileExtension().equals("java") || file.getFileExtension().equals("txt"))) {

						String content = readFile(file);
						Document doc = new Document();
						doc.add(new StringField("filename", file.getName(), Field.Store.YES));
						doc.add(new TextField("content", content, Field.Store.YES));
						try {
							writer.addDocument(doc);
						} catch (IOException ioException) {
							System.err.println(
									"Failed to index file: " + file.getName() + " — " + ioException.getMessage());
						}
					}
					return true;
				});
			}
			indexedProjects.put(project, true);
		} catch (Exception e) {
			System.err.println("Indexing failed for project " + project.getName() + ": " + e.getMessage());
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
					sb.append("// File: ").append(doc.get("filename")).append("\n");
					sb.append(doc.get("content")).append("\n\n");
				}
				return sb.toString();
			}
		} catch (Exception e) {
			return "";
		}
	}

	private static Path getIndexPath(IProject project) {
		String userHome = System.getProperty("user.home");
		String projectName = project.getName();
		return Paths.get(userHome, ".localllama", "LocalLlamaAssist", projectName, "database", "knowledge_index");
	}

	public static List<String> getFilesUsingSymbol(String symbol) {
		Set<String> files = symbolToFiles.getOrDefault(symbol, Collections.emptySet());
		return new ArrayList<>(files);
	}

	public static String getFileContent(String filename) {
		for (Map.Entry<IFile, String> entry : fileContents.entrySet()) {
			if (entry.getKey().getName().equals(filename)) {
				return entry.getValue();
			}
		}
		return "";
	}

	public static List<String> getIndexedFilePaths() {
		return new ArrayList<>(indexedFilePaths);
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
			return "";
		}
	}
}
