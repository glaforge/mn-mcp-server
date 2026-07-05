# 🌕 Moon Phases MCP Server (with Micronaut)

This project provides an **MCP (Model Context Protocol)** server that calculates the phases of the moon for any given date. It is built using the robust [Micronaut](https://micronaut.io) framework and implements modern Java best practices.

## 🚀 Features
- Exposes two distinct tools:
  - `current-moon-phase`: Retrieves the moon phase for today.
  - `moon-phase-at-date`: Calculates the moon phase for a specific date (`yyyy-MM-dd`).
- Fully leverages **Micronaut 5.0** running on **Java 25**.
- Integrated with [Google ADK](https://github.com/google/adk) and [LangChain4j](https://github.com/langchain4j/langchain4j) for advanced LLM agent testing.
- Ready for containerized deployment to Google Cloud Run.

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- **JDK 25** or higher (required by Micronaut 5.x)
- The [Google Cloud SDK](https://cloud.google.com/sdk/install) (`gcloud`) (if deploying)
- [Node.js](https://nodejs.org/en/download/) (for `npx`) to test via the MCP inspector

## 🛠️ Running Locally

Start the server locally via the Gradle Wrapper:

```bash
./gradlew run
```
The server will start and be available at `http://localhost:8080`.

## 🧪 Testing

The project includes integration tests that verify the MCP tools against live Google Gemini LLMs (using LangChain4j and the Google ADK). 
To run the full test suite, you must provide your Gemini API key:

```bash
export GEMINI_API_KEY="your-api-key-here"
./gradlew test
```
*(If the `GEMINI_API_KEY` is missing, the LLM-dependent tests will gracefully skip.)*

### Testing with the MCP Inspector

You can quickly interact with the server's endpoints using the official MCP Inspector tool:

```bash
npx @modelcontextprotocol/inspector
```
This opens a browser interface. Connect it to your local server at: `http://localhost:8080/mcp`.

---

## ☁️ Deploying to Google Cloud Run

### 1. Google Cloud Setup

If you haven't already, authenticate and create your project:

```bash
gcloud auth login
gcloud projects create my-micronaut-mcp-server
export PROJECT_ID=$(gcloud config get-value project)
export SERVICE_NAME=mn-mcp-server
export REGION=europe-west1
# Enable billing in the web console if needed
```

### 2. Enable Services

Enable the required APIs:

```bash
gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com
```

### 3. Deploy

Deploy the application directly from your source directory:

```bash
gcloud run deploy $SERVICE_NAME \
    --source . \
    --platform managed \
    --region $REGION \
    --allow-unauthenticated
```
You will receive a URL for your deployed service (e.g., `https://mn-mcp-server-12345.europe-west1.run.app`).

## 🤖 Using with Google Antigravity

### Method 1: Visual Configuration (Recommended)
If you are using Antigravity 2.0 or the Antigravity IDE:
1. Click the **Settings** button (gear icon) in the bottom left of your screen.
2. Navigate to **Customizations** and locate the **Installed MCP Servers** section.
3. Click **Add MCP** to add a custom remote server.
4. Provide the URL of your deployed Cloud Run service, appending the `/mcp` path (e.g., `https://mn-mcp-server-12345.europe-west1.run.app/mcp`).

### Method 2: Manual Configuration (Power Users)
You can also configure the server manually by editing the global `mcp_config.json` file (typically located at `~/.gemini/config/mcp_config.json`):

```json
{
  "mcpServers": {
    "moonPhases": {
      "serverUrl": "<YOUR_CLOUD_RUN_URL>/mcp"
    }
  }
}
```

### Method 3: Workspace Local Configuration
If you want to configure the server only for a specific project workspace (for example, while developing it locally), you can create an `.agents/mcp_config.json` file at the root of your project:

```json
{
  "mcpServers": {
    "moonPhasesLocal": {
      "serverUrl": "http://localhost:8080/mcp"
    }
  }
}
```

Once configured using any of these methods, the agent will have access to your tools across the Antigravity CLI, IDE, and Desktop App. Simply ask:
> *"What's the current phase of the moon?"*

---

## 🧠 How It Works

The core calculation logic lives in the `MoonPhasesService`:

```java
@Singleton
public class MoonPhasesService {
    public MoonPhaseEmoji currentMoonPhase() { /* ... */ }
    public MoonPhaseEmoji moonPhaseAtDate(LocalDate localDate) { /* ... */ }
}
```

It returns a `MoonPhaseEmoji` record annotated for **JSON Schema** generation:

```java
@JsonSchema(title = "Phase of the moon", uri = "/moonPhase")
@Serdeable
public record MoonPhaseEmoji(
    MoonPhase phase,
    @NotBlank String emoji
) { }
```

The `MoonPhasesMcpServer` seamlessly exposes these methods as MCP tools using the `@Tool` annotation, relying on Micronaut's built-in validation:

```java
@Singleton
public class MoonPhasesMcpServer {
    @Inject
    MoonPhasesService moonPhasesService;

    @Tool(name = "current-moon-phase", description = "Provides the current moon phase")
    public MoonPhaseEmoji currentMoonPhase() {
        return moonPhasesService.currentMoonPhase();
    }

    @Tool(name = "moon-phase-at-date", description = "Provides the moon phase at a certain date (yyyy-MM-dd)")
    @NotNull
    public MoonPhaseEmoji moonPhaseAtDate(@Valid MoonPhaseRequest moonPhaseRequest) {
        return moonPhasesService.moonPhaseAtDate(moonPhaseRequest.date());
    }
}
```

## 🏗️ Recreating the Project

If you wish to bootstrap a similar Micronaut 5 application from scratch, you can use the `mn` CLI:

```bash
mn create-app --build=gradle --jdk=25 --lang=java --test=junit \
  --features=jackson-databind,json-schema,validation,json-schema-validation mn.mcp.server.mn-mcp-server
```

### Added Dependencies

The project relies on these specialized dependencies in `build.gradle` for MCP and LLM integration:

```groovy
dependencies {
    // Micronaut MCP Server SDK
    implementation("io.micronaut.mcp:micronaut-mcp-server-java-sdk:1.1.0")

    // Pinned core MCP SDK (compatible with Micronaut 1.1.0)
    implementation("io.modelcontextprotocol.sdk:mcp:1.1.3")

    // Rich JSON schema handling
    annotationProcessor("io.micronaut.jsonschema:micronaut-json-schema-processor:2.0.1")
    implementation("io.micronaut.jsonschema:micronaut-json-schema-annotations:2.0.1")

    // LLM Agent / LangChain4j Integration
    implementation("com.google.adk:google-adk:1.5.0")
    implementation("dev.langchain4j:langchain4j-google-genai:1.17.1-beta27")
    testImplementation("dev.langchain4j:langchain4j-mcp:1.17.0-beta27")
}
```

---

This project is licensed under the [Apache 2 license](LICENSE).

> [!NOTE]
> This is not an official Google project.