package com.ollamassist.plugin;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		// Place ChatView to the right of the editor
		layout.addView("com.ollamassist.plugin.ui.ChatView", IPageLayout.LEFT, 0.75f, editorArea);

		// Optional: make it non-closeable
		layout.getViewLayout("com.ollamassist.plugin.ui.ChatView").setCloseable(false);
	}
}
