package com.localllama.plugin.util;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.IDocument;

public class EclipseEditorUtil {

    public static String getActiveEditorContent() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
                IEditorPart editor = page.getActiveEditor();
                if (editor instanceof ITextEditor) {
                    ITextEditor textEditor = (ITextEditor) editor;
                    IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
                    return document.get();
                }
            }
        }
        return null;
    }
}
