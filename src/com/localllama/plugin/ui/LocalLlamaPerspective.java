package com.localllama.plugin.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class LocalLlamaPerspective implements IPerspectiveFactory {

	public static final String ID = "com.localllama.plugin.ui.LocalLlamaPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// 🔹 Place ChatView on the left
		layout.addView(ChatView.ID, IPageLayout.LEFT, 0.25f, editorArea);
	}
}