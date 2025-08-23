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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class ProjectDependencyTracker {

	private static final Map<String, Set<String>> symbolToFiles = new HashMap<>();
	private static final Map<IFile, String> fileContents = new HashMap<>();

	public static void indexWorkspace() {
		symbolToFiles.clear();
		fileContents.clear();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		try {
			root.accept(resource -> {
				if (resource instanceof IFile && "java".equals(resource.getFileExtension())) {
					IFile file = (IFile) resource;
					String content = readFile(file);
					fileContents.put(file, content);

					Set<String> symbols = extractSymbols(content);
					for (String symbol : symbols) {
						symbolToFiles.computeIfAbsent(symbol, k -> new HashSet<>()).add(file.getName());
					}
				}
				return true;
			});
		} catch (CoreException e) {
			System.err.println("Error indexing workspace: " + e.getMessage());
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

	private static Set<String> extractSymbols(String content) {
		Set<String> symbols = new HashSet<>();

		Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)");
		Matcher classMatcher = classPattern.matcher(content);
		while (classMatcher.find()) {
			symbols.add(classMatcher.group(1));
		}

		Pattern methodPattern = Pattern.compile("\\b(?:public|private|protected)?\\s+\\w+\\s+(\\w+)\\s*\\(");
		Matcher methodMatcher = methodPattern.matcher(content);
		while (methodMatcher.find()) {
			symbols.add(methodMatcher.group(1));
		}

		return symbols;
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
