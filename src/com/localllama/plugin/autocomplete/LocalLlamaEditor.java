package com.localllama.plugin.autocomplete;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.editors.text.TextEditor;

public class LocalLlamaEditor extends TextEditor {

	public LocalLlamaEditor() {
		setSourceViewerConfiguration(new SourceViewerConfiguration() {
			@Override
			public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
				ContentAssistant assistant = new ContentAssistant();
				assistant.setContentAssistProcessor(new LocalLlamaContentAssistProcessor(),
						IDocument.DEFAULT_CONTENT_TYPE);
				assistant.enableAutoActivation(true);
				assistant.setAutoActivationDelay(0);
				assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
				assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
				return assistant;
			}
		});
	}
}
