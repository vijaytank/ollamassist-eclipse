package com.localllama.plugin.rag;

import com.localllama.plugin.util.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

public class ResourceChangeListener implements IResourceChangeListener {

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            try {
                event.getDelta().accept(delta -> {
                    IResource resource = delta.getResource();
                    if (resource instanceof IFile) {
                        if (delta.getKind() == IResourceDelta.CHANGED) {
                            Logger.log("Resource changed: " + resource.getName());
                            ActiveFileIndexer.indexFile((IFile) resource);
                        }
                    }
                    return true; // visit children
                });
            } catch (Exception e) {
                Logger.error("Error processing resource change", e);
            }
        }
    }
}
