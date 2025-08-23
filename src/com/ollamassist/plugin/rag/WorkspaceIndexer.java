package com.ollamassist.plugin.rag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class WorkspaceIndexer {

    public static List<String> indexWorkspace() {
        List<String> files = new ArrayList<>();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        try {
            for (IProject project : root.getProjects()) {
                if (project.isOpen()) {
                    collectFiles(project, files);
                }
            }
        } catch (CoreException e) {
            files.add("Error indexing workspace: " + e.getMessage());
        }

        return files;
    }

    private static void collectFiles(IContainer container, List<String> files) throws CoreException {
        for (IResource res : container.members()) {
            if (res instanceof IFile && res.getFileExtension() != null &&
                (res.getFileExtension().equals("java") || res.getFileExtension().equals("txt"))) {
                files.add(((IFile) res).getLocation().toOSString());
            } else if (res instanceof IContainer) {
                collectFiles((IContainer) res, files);
            }
        }
    }
}
