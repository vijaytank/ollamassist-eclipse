package com.localllama.plugin.autocomplete;

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
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.localllama.plugin.util.Logger;

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
		String suggestion = LocalLlamaAutocompleteClient.getSuggestion(content);
		if (suggestion != null && !suggestion.isEmpty()) {
			try {
				int line = document.getLineOfOffset(offset);
				int lineOffset = document.getLineOffset(line);
				String prefix = document.get(lineOffset, offset - lineOffset);
				String displayString = suggestion.startsWith(prefix) ? suggestion.substring(prefix.length())
						: suggestion;
				suggestions.add(new CompletionProposal(displayString, offset, 0, displayString.length()));
			} catch (BadLocationException e) {
				Logger.error("Error adjusting suggestion", e);
			}
		}
		isLoading = false;
		return suggestions.toArray(new ICompletionProposal[0]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.' };
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
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
}
