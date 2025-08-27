package com.localllama.plugin.autocomplete;

import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.util.Logger;
import com.localllama.plugin.util.ModelSelectorUtil;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class LocalLlamaContentAssistProcessor implements IContentAssistProcessor {

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        Logger.log("Computing completion proposals...");
        IDocument document = viewer.getDocument();
        String content = document.get();

        List<ICompletionProposal> proposals = new ArrayList<>();
        String suggestion = LocalLlamaAutocompleteClient.getSuggestion(content);
        if (suggestion != null && !suggestion.isEmpty()) {
            Logger.log("Got suggestion: " + suggestion);
            String displayString = suggestion;
            try {
                int line = document.getLineOfOffset(offset);
                int lineOffset = document.getLineOffset(line);
                String prefix = document.get(lineOffset, offset - lineOffset).trim();
                if (suggestion.startsWith(prefix)) {
                    displayString = suggestion.substring(prefix.length());
                }
            } catch (BadLocationException e) {
                Logger.error("Error adjusting suggestion", e);
            }
            proposals.add(new CompletionProposal(displayString, offset, 0, displayString.length()));
        } else {
            Logger.log("No suggestion found");
        }
        return proposals.toArray(new ICompletionProposal[0]);

    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[]{'.'};
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public org.eclipse.jface.text.contentassist.IContextInformationValidator getContextInformationValidator() {
        return null;
    }
}
