# LocalLlama Plugin for Eclipse

LocalLlama is a privacy-first AI assistant designed to integrate seamlessly with Eclipse. Powered by local Ollama models, it enhances developer productivity through intelligent code interactions, commit message generation, and smart autocompletion—all without sending data to the cloud.

---

## 🚀 Features

-   **In-IDE Chat with Ollama Models**: Ask questions, get explanations, and discuss your code—all within Eclipse. Context-aware responses are powered by RAG (Retrieval-Augmented Generation) using your indexed project sources.
-   **Commit Message Generation**: Automatically generate Conventional Commit messages based on your current Git diff using customizable prompts.
-   **Smart Code Autocomplete**: Trigger AI-powered suggestions in the Java editor. Suggestions are context-aware and dismissible.
-   **High-Performance Workspace Indexing**: All open projects are indexed in parallel for fast, symbol-aware retrieval. Supports indexing of `.java` source files within projects and `.jar` dependencies.
-   **Setup Wizard**: First-time users are guided through model selection, workspace indexing, and configuration via a multi-step wizard.
-   **Dynamic Model Selection**: Automatically fetches and lists available models from your Ollama instance in the preferences page.
-   **Customizable Preferences**: Configure the model, Ollama endpoint, and logging options via **Window > Preferences > LocalLlama**.
-   **Offline Mode**: All features work offline once the model and data are loaded. Your workflow remains uninterrupted.

---

## 🧠 How It Works

-   Uses local Ollama models (e.g., llama3.1) for inference.
-   Indexes your project files and dependencies in parallel using a multi-threaded process powered by Lucene for fast and efficient context-aware RAG.
-   Communicates with Ollama via its local HTTP API (defaults to `http://localhost:11434`).
-   No internet connection is required after initial setup.

---

## 🖥️ Requirements

| Component | Minimum Requirement |
| :--- | :--- |
| **CPU** | 64-bit, 4+ cores (AVX/AVX2 recommended) |
| **RAM** | 8GB for 7B models, 16GB+ for 13B models |
| **Storage** | 15GB+ for Ollama + model files |
| **OS** | Windows 10 22H2+, macOS Monterey+, or Linux with glibc 2.17+ |
| **Eclipse** | Eclipse IDE 2023-12 or newer |
| **Java** | JavaSE-21 or compatible JDK |

---

## 🧭 Getting Started

1.  **Install Ollama**  
    Download and run Ollama from [https://ollama.com](https://ollama.com). Ensure the Ollama server is running in the background.

2.  **Download a Model**  
    Open a terminal or command prompt and pull a model. For example:
    ```bash
    ollama run llama3.1
    ```

3.  **Install the LocalLlama Plugin**
    -   In Eclipse, go to **Help > Eclipse Marketplace...**.
    -   Search for "LocalLlama" and click **Install**.
    -   Restart Eclipse when prompted.

4.  **Configure the Plugin**
    -   Open **Window > Preferences > LocalLlama**.
    -   Verify the **Ollama Endpoint** is correct (usually `http://localhost:11434`).
    -   Click the **Model** dropdown to see a list of your downloaded Ollama models. Select the one you wish to use.
    -   Enable logging if you want to see detailed output in the Eclipse Error Log view.
    -   Click **Apply and Close**.

5.  **Start Coding**
    -   The plugin will automatically start indexing your open projects in the background.
    -   Use the **LocalLlama View** for chat (reopen it from **Window > Show View > Other... > LocalLlama**).
    -   Trigger autocomplete in the Java editor as you type.

---

## 🛠️ Development Notes

-   Built with Eclipse RCP and SWT.
-   Uses `plugin.xml` for registering views, editors, perspectives, and commands.
-   Employs `ExecutorService` for concurrent indexing of project files and JARs to maximize performance.
-   JSON parsing is handled via `org.json`.
-   Uses Lucene for the indexing engine (`lucene-core`, `lucene-queryparser`, `lucene-analysis-common`).
-   Icons are located in the `icons/` folder and are compatible with the Eclipse dark theme.
-   `LocalLlamaPreferenceStore` manages persistent settings.

---

## 📄 License

MIT License.

---

## 🤝 Contributing

We welcome contributions! Please open issues for bugs, feature requests, or improvements. To contribute:
1.  Fork the repository.
2.  Create a feature branch.
3.  Submit a pull request with a clear description of your changes.

---

## 🔒 Privacy First

LocalLlama runs entirely on your machine. No code, metadata, or queries are ever sent to the cloud or any external server. Your workflow stays private and secure.
