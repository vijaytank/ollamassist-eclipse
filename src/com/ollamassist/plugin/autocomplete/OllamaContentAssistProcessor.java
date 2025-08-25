package com.ollamassist.plugin.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.ollamassist.plugin.util.ModelSelectorUtil;
import com.ollamassist.plugin.util.OllamaQueryUtil;

public class OllamaContentAssistProcessor implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument doc = viewer.getDocument();
		String prefix = getPrefix(doc, offset);
		String suggestion = OllamaAutocompleteClient.getSuggestion(prefix);

		if (suggestion == null || suggestion.isEmpty() || suggestion.equals(prefix)) {
			return new ICompletionProposal[0];
		}

		String completion = suggestion.substring(prefix.length());

		return new ICompletionProposal[] { new CompletionProposal(completion, offset, 0, completion.length()) };
	}

	private String getPrefix(IDocument doc, int offset) {
		try {
			int start = offset;
			while (start > 0 && Character.isJavaIdentifierPart(doc.getChar(start - 1))) {
				start--;
			}
			return doc.get(start, offset - start);
		} catch (BadLocationException e) {
			return "";
		}
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return new IContextInformation[0];
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { ' ' };
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return new IContextInformationValidator() {
			@Override
			public void install(IContextInformation info, ITextViewer viewer, int offset) {
			}

			@Override
			public boolean isContextInformationValid(int offset) {
				return true;
			}
		};
	}

	public static List<ICompletionProposal> generateProposals(String prefix, int offset) {
		String model = ModelSelectorUtil.getDefaultModel();
		String suggestion = OllamaQueryUtil.blockingQuery(prefix, model);

		if (suggestion == null || suggestion.isEmpty() || suggestion.equals(prefix)) {
			return new ArrayList<>();
		}

		String completion = suggestion.substring(prefix.length());
		ICompletionProposal proposal = new CompletionProposal(completion, offset, 0, completion.length());
		return List.of(proposal);
	}

}
