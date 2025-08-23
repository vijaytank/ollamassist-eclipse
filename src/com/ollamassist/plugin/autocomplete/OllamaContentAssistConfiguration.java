package com.ollamassist.plugin.autocomplete;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.*;

public class OllamaContentAssistConfiguration extends SourceViewerConfiguration {

    @Override
    public IContentAssistant getContentAssistant(org.eclipse.jface.text.source.ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();
        assistant.setContentAssistProcessor(new OllamaContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
        assistant.enableAutoActivation(true);
        assistant.setAutoActivationDelay(0);
        assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
        assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
        return assistant;
    }
}
