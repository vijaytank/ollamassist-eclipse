package com.ollamassist.plugin.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class OllamPerspective implements IPerspectiveFactory {

	public static final String ID = "com.ollamassist.plugin.ui.OllamPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// 🔹 Place ChatView on the left
		layout.addView(ChatView.ID, IPageLayout.LEFT, 0.25f, editorArea);
	}
}
