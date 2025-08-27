package com.localllama.plugin.autocomplete;

import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.util.LocalLlamaQueryUtil;
import com.localllama.plugin.util.Logger;
import com.localllama.plugin.util.ModelSelectorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class LocalLlamaContentAssistProcessor implements IContentAssistProcessor {

    private List<ICompletionProposal> suggestions = Collections.synchronizedList(new ArrayList<>());
    private boolean isLoading = false;

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        Logger.log("Computing completion proposals...");

        if (!suggestions.isEmpty()) {
            ICompletionProposal[] result = suggestions.toArray(new ICompletionProposal[0]);
            suggestions.clear();
            return result;
        }

        if (isLoading) {
            return new ICompletionProposal[0];
        }

        IDocument document = viewer.getDocument();
        String content = document.get();

        isLoading = true;
        LocalLlamaQueryUtil.asyncQuery(content, ModelSelectorUtil.getDefaultModel(), suggestion -> {
            if (suggestion != null && !suggestion.isEmpty()) {
                Logger.log("Got suggestion: " + suggestion);
                String displayString = suggestion;
                try {
                    int line = document.getLineOfOffset(offset);
                    int lineOffset = document.getLineOffset(line);
                    String prefix = document.get(lineOffset, offset - lineOffset);
                    if (suggestion.startsWith(prefix)) {
                        displayString = suggestion.substring(prefix.length());
                    }
                } catch (BadLocationException e) {
                    Logger.error("Error adjusting suggestion", e);
                }
                suggestions.add(new CompletionProposal(displayString, offset, 0, displayString.length()));
            }
            isLoading = false;
            viewer.getTextWidget().getDisplay().asyncExec(() -> viewer.getContentAssistant().showPossibleCompletions());
        });

        return new ICompletionProposal[0];
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
