package com.ollamassist.plugin.autocomplete;

import org.eclipse.ui.editors.text.TextEditor;

public class OllamaEditor extends TextEditor {

	public OllamaEditor() {
		super();
		setSourceViewerConfiguration(new OllamaContentAssistConfiguration());
		setDocumentProvider(new org.eclipse.ui.editors.text.FileDocumentProvider());
	}
}
