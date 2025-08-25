package com.ollamassist.plugin.autocomplete;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class OllamaProposalComputer implements IJavaCompletionProposalComputer {

    @Override
    public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
        if (!(context instanceof JavaContentAssistInvocationContext)) {
            return new ArrayList<>();
        }

        JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;
        IDocument doc = javaContext.getDocument();
        int offset = javaContext.getInvocationOffset();
        String prefix = extractPrefix(doc, offset);

        return OllamaContentAssistProcessor.generateProposals(prefix, offset);
    }

    @Override
    public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
        return new ArrayList<>();
    }

    private String extractPrefix(IDocument doc, int offset) {
        try {
            int start = offset;
            while (start > 0 && Character.isJavaIdentifierPart(doc.getChar(start - 1))) {
                start--;
            }
            return doc.get(start, offset - start);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public void sessionStarted() {}

    @Override
    public void sessionEnded() {}
}
