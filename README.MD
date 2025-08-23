# OllamAssist Plugin for Eclipse

**OllamAssist** is a privacy-first AI assistant designed to integrate seamlessly with Eclipse. Powered by local Ollama models, it enhances developer productivity through intelligent code interactions, commit message generation, and smart autocompletion—all without sending data to the cloud.

---

## 🚀 Features

- **In-IDE Chat with Ollama Models**  
  Ask questions, get explanations, and discuss your code—all within Eclipse.  
  Context-aware responses powered by RAG (Retrieval-Augmented Generation) using your indexed project sources.

- **Commit Message Generation**  
  Automatically generate Conventional Commit messages based on your current Git diff.

- **Smart Code Autocomplete (Experimental)**  
  Press `Shift+Space` to trigger AI-powered suggestions.  
  - `Enter` to insert  
  - Any other key to dismiss

- **Offline Mode**  
  All features work offline once the model and data are loaded.

- **Customizable Model Settings**  
  Choose your preferred Ollama model from the plugin's Settings page.

---

## 🧠 How It Works

- Uses local Ollama models (e.g. `llama3.1`) for inference  
- Indexes your project files for context-aware RAG  
- Communicates with Ollama via local HTTP API  
- No internet connection required after setup

---

## 🖥️ Requirements

| Component   | Minimum Requirement                                          |
|-------------|--------------------------------------------------------------|
| **CPU**     | 64-bit, 2+ cores (AVX/AVX2/AVX512 recommended)               |
| **RAM**     | 8GB for 7B models, 16GB+ for 13B models                      |
| **Storage** | 12GB+ for Ollama + model files                               |
| **OS**      | Windows 10 22H2+, macOS Monterey+, or Linux with glibc 2.17+ |
| **Eclipse** | Eclipse IDE 2023-12 or newer                                 |
| **Java**    | JavaSE-21 or compatible JDK                                  |


---

## 🧭 Getting Started

1. **Install Ollama**  
   [Download Ollama](https://ollama.com) and start the local server.

2. **Download a Model**  
   ```bash
   ollama run llama3.1
   ```

3. **Install OllamAssist Plugin**  
   - Open Eclipse  
   - Go to `Help > Eclipse Marketplace`  
   - Search for `"OllamAssist"`  
   - Click **Install**

4. **Configure the Plugin**  
   - Open `Window > Preferences > OllamAssist`  
   - Select your model (e.g. `llama3.1`)  
   - Set your workspace indexing options

5. **Start Coding**  
   Use the OllamAssist view or autocomplete shortcuts to interact with your AI assistant.

---

## 🛠️ Development Notes

- Built with Eclipse RCP and SWT  
- Uses `plugin.xml` for view and perspective registration  
- JSON parsing via `json-20240303.jar`  
- Icon located at `icons/icon.png`  
- Compatible with Eclipse dark theme

---

## 📄 License

MIT License. See `LICENSE.md` for details.

---

## 🤝 Contributing

We welcome contributions! Please open issues for bugs, feature requests, or improvements.

---

## 🔒 Privacy First

OllamAssist runs entirely on your machine. No code, metadata, or queries are sent externally. Your workflow stays private.

---

```