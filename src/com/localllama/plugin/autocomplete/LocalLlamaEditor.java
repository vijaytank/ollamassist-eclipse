package com.localllama.plugin.autocomplete;

import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;

public class LocalLlamaEditor extends TextEditor {

	public LocalLlamaEditor() {
		super();
		setSourceViewerConfiguration(new LocalLlamaContentAssistConfiguration());
		setDocumentProvider(new FileDocumentProvider());
	}
}
