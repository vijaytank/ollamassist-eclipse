package com.localllama.plugin.autocomplete;

import java.util.ArrayList;
import java.util.List;

public class LocalLlamaSuggestionProvider {

	public static final String ID = "com.localllama.plugin.autocomplete.proposals";

	public List<String> getSuggestions(String prefix) {
		List<String> suggestions = new ArrayList<>();

		if (prefix == null || prefix.isEmpty()) {
			return suggestions;
		}

		// Basic static suggestions — can be extended with Lucene search or dynamic
		// logic
		if (prefix.startsWith("sys")) {
			suggestions.add("system.out.println");
			suggestions.add("system.gc()");
		} else if (prefix.startsWith("imp")) {
			suggestions.add("import java.util.*;");
			suggestions.add("import org.eclipse.ui.*;");
		} else {
			suggestions.add(prefix + "_option1");
			suggestions.add(prefix + "_option2");
		}

		return suggestions;
	}
}
