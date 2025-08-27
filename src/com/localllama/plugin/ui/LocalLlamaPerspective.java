package com.localllama.plugin.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.localllama.plugin.view.LocalLlamaView;

public class LocalLlamaPerspective implements IPerspectiveFactory {

	public static final String ID = "com.localllama.plugin.ui.LocalLlamaPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// 🔹 Place LocalLlamaView on the left
		layout.addView(LocalLlamaView.ID, IPageLayout.LEFT, 0.25f, editorArea);
	}
}