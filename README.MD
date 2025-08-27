```
# LocalLlama Plugin for Eclipse

LocalLlama is a privacy-first AI assistant designed to integrate seamlessly with Eclipse. Powered by local Ollama models, it enhances developer productivity through intelligent code interactions, commit message generation, and smart autocompletion—all without sending data to the cloud.

---

## 🚀 Features

- In-IDE Chat with Ollama Models  
  Ask questions, get explanations, and discuss your code—all within Eclipse.  
  Context-aware responses powered by RAG (Retrieval-Augmented Generation) using your indexed project sources.

- Commit Message Generation  
  Automatically generate Conventional Commit messages based on your current Git diff using customizable prompts.

- Smart Code Autocomplete  
  Trigger AI-powered suggestions via:  
  - Shift+Space in the LocalLlama Editor  
  - Java autocomplete via Eclipse JDT integration  
  Suggestions are context-aware and dismissible with any key other than Enter.

- Workspace Indexing  
  Index all open projects for symbol-aware retrieval.  
  Supports .java and .txt files using Lucene.

- Setup Wizard  
  First-time users are guided through model selection, workspace indexing, and configuration via a multi-step wizard.

- Customizable Preferences  
  Configure model, endpoint, commit prompt, and indexing options via Window > Preferences > LocalLlama.

- Offline Mode  
  All features work offline once the model and data are loaded.

---

## 🧠 How It Works

- Uses local Ollama models (e.g. llama3.1) for inference  
- Indexes your project files using Lucene for context-aware RAG  
- Communicates with Ollama via local HTTP API (http://localhost:11434)  
- No internet connection required after setup

---

## 🖥️ Requirements

Component   | Minimum Requirement
------------|--------------------------------------------------------------
CPU         | 64-bit, 2+ cores (AVX/AVX2/AVX512 recommended)
RAM         | 8GB for 7B models, 16GB+ for 13B models
Storage     | 12GB+ for Ollama + model files
OS          | Windows 10 22H2+, macOS Monterey+, or Linux with glibc 2.17+
Eclipse     | Eclipse IDE 2023-12 or newer
Java        | JavaSE-21 or compatible JDK

---

## 🧭 Getting Started

1. Install Ollama  
   Download Ollama from https://ollama.com and start the local server.

2. Download a Model  
  
   ollama run llama3.1
  
3. Install LocalLlama Plugin  
   - Open Eclipse  
   - Go to Help > Eclipse Marketplace  
   - Search for "LocalLlama"  
   - Click Install

4. Configure the Plugin  
   - Open Window > Preferences > LocalLlama  
   - Select your model (e.g. llama3.1)  
   - Set your workspace indexing options  
   - Customize commit message prompt if needed

5. Start Coding  
   - Use the LocalLlama View for chat  
   - Use the LocalLlama Editor or Java editor for autocomplete  
   - Use the Help menu to reopen the chat view anytime

---

## 🛠️ Development Notes

- Built with Eclipse RCP and SWT  
- Uses plugin.xml for view, editor, perspective, and command registration  
- JSON parsing via json-20240303.jar  
- Lucene-based indexing via lucene-core, lucene-queryparser, and lucene-analysis-common  
- Icons located in icons/ folder  
- Compatible with Eclipse dark theme  
- Uses LocalLlamaPreferenceStore for persistent settings  
- Autocomplete integrated via IJavaCompletionProposalComputer and ContentAssistProcessor

---

## 📄 License

MIT License. See LICENSE.md for details.

---

## 🤝 Contributing

We welcome contributions! Please open issues for bugs, feature requests, or improvements.  
To contribute:  
- Fork the repo  
- Create a feature branch  
- Submit a pull request with a clear description

---

## 🔒 Privacy First

LocalLlama runs entirely on your machine.  
No code, metadata, or queries are sent externally.  
Your workflow stays private and secure.
```