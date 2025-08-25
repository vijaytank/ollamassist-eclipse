package com.ollamassist.plugin.rag;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.ollamassist.plugin.util.SymbolExtractor;

public class ProjectDependencyTracker {

	private static final Map<String, Set<String>> symbolToFiles = new HashMap<>();
	private static final Map<IFile, String> fileContents = new HashMap<>();
	private static final List<String> indexedFilePaths = new ArrayList<>();

	public static void indexWorkspace() {
		symbolToFiles.clear();
		fileContents.clear();
		indexedFilePaths.clear();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		try {
			for (IProject project : root.getProjects()) {
				if (project.isOpen()) {
					collectFiles(project);
				}
			}
		} catch (CoreException e) {
			System.err.println("Error indexing workspace: " + e.getMessage());
		}
	}

	private static void collectFiles(IContainer container) throws CoreException {
		for (IResource res : container.members()) {
			if (res instanceof IFile && res.getFileExtension() != null
					&& (res.getFileExtension().equals("java") || res.getFileExtension().equals("txt"))) {

				IFile file = (IFile) res;
				indexedFilePaths.add(file.getLocation().toOSString());

				String content = readFile(file);
				fileContents.put(file, content);

				Set<String> symbols = SymbolExtractor.extractFromCode(content);
				for (String symbol : symbols) {
					symbolToFiles.computeIfAbsent(symbol, k -> new HashSet<>()).add(file.getName());
				}

			} else if (res instanceof IContainer) {
				collectFiles((IContainer) res);
			}
		}
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
