package com.localllama.plugin.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;

import com.localllama.plugin.service.LocalLlamaClient;
import com.localllama.plugin.util.ModelSelectorUtil;

public class LocalLlamaContentAssistProcessor implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		String prefix = "";
		try {
			int line = document.getLineOfOffset(offset);
			int lineOffset = document.getLineOffset(line);
			prefix = document.get(lineOffset, offset - lineOffset);
		} catch (BadLocationException e) {
			e.printStackTrace(); // Consider logging in production
		}

		List<ICompletionProposal> proposals = new ArrayList<>();
		String suggestion = LocalLlamaAutocompleteClient.getSuggestion(prefix);
		if (suggestion != null && !suggestion.isEmpty()) {
			proposals.add(new CompletionProposal(suggestion, offset, 0, suggestion.length()));
		}
		return proposals.toArray(new ICompletionProposal[0]);

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
	public org.eclipse.jface.text.contentassist.IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public static List<ICompletionProposal> generateProposals(String prefix, int offset) {
		String model = ModelSelectorUtil.getDefaultModel();
		String suggestion = LocalLlamaClient.blockingQuery(prefix, model);

		if (suggestion == null || suggestion.isEmpty() || suggestion.equals(prefix)) {
			return new ArrayList<>();
		}

		String completion = suggestion.substring(Math.min(prefix.length(), suggestion.length()));
		ICompletionProposal proposal = new CompletionProposal(completion, offset, 0, completion.length());
		return List.of(proposal);
	}

}
