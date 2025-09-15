# A simple HTTP Streamable MCP server with Micronaut

The project creates an MCP (Model Context Protocol) server to calculate the phases of the moon,
and is implemented with the [Micronaut](https://micronaut.io) framework.

## Prerequisites

Before you begin, ensure you have the following installed:
*   JDK 21 or higher
*   The [Google Cloud SDK](https://cloud.google.com/sdk/install) (`gcloud`)
*   [Node.js](https://nodejs.org/en/download/) (which includes `npx`) for testing with the MCP inspector

## Running the MCP server locally

To run the server on your local machine, execute the following command:

```bash
./gradlew run
```
The server will start on `http://localhost:8080`.

## Testing the MCP server with MCP inspector

You can test the MCP endpoints using the MCP Inspector tool:

```bash
npx @modelcontextprotocol/inspector
```

This command will open a web browser, allowing you to connect to the server's MCP endpoint at `http://localhost:8080/mcp`.

You'll be able to try the two MCP tools defined by the server:
* `current-moon-phase`: to get the current phase of the moon as of today (no argument required)
* `moon-phase-at-date`: to get the phase of the moon at a particular date (a string following the `yyyy-MM-dd` date format)

## Deploying to Google Cloud Run

### 1. Initial Google Cloud Setup

If you haven't already, authenticate with gcloud, create a new project, and link a billing account.

```bash
# Log in to your Google account
gcloud auth login

# Create a new project (or use an existing one)
gcloud projects create my-micronaut-mcp-server # Choose a unique project ID

# Set your project for the current session
export PROJECT_ID=$(gcloud config get-value project)
echo "Using project: $PROJECT_ID"

# Enable billing for the project (interactive step)
# See: https://cloud.google.com/billing/docs/how-to/modify-project
```

### 2. Enable Required Services

Enable the APIs for Cloud Run, Cloud Build, and Artifact Registry.

```bash
gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com
```

### 3. Deploy the Service

Deploy the application directly from your local source code. Choose a region for your deployment.

```bash
# Set your desired region
export REGION="europe-west1"

# Deploy from source
gcloud run deploy mn-mcp-server \
    --source . \
    --platform managed \
    --region $REGION \
    --allow-unauthenticated
```

After a few minutes, the command will output the URL for your deployed service. It will look something like: `https://mn-mcp-server-12345.europe-west1.run.app`.

## Configuring your MCP server in Gemini CLI

You can configure the Gemini CLI to use your newly deployed MCP server.

First, get the URL from the deployed service:
```bash
export SERVICE_URL=$(gcloud run services describe mn-mcp-server --platform managed --region $REGION --format 'value(status.url)')
```

Now, add it to the Gemini CLI configuration:
```bash
gemini mcp add moonPhases \
    --transport http \
    $SERVICE_URL/mcp
```

You can now invoke the tool in Gemini CLI by asking: `What's the current phase of the moon?` or `What was the moon phase on 1969-07-20?`.

## How it Works

The core logic is handled by `MoonPhasesService`, which calculates the moon phase for a given date.

```java
@Singleton
public class MoonPhasesService {
    // ...
    public MoonPhase currentMoonPhase() { /*...*/ }
    public MoonPhase moonPhaseAtUnixTimestamp(long timeSeconds) { /*...*/ }
}
```

This service returns a `MoonPhase` record, which is annotated for JSON Schema generation:

```java
@JsonSchema
@Introspected
public record MoonPhase(
    @NotBlank String phase,
    @NotBlank String emoji
) { }
```

The `MoonPhasesMcpServer` class exposes the service's methods as MCP tools using the `@Tool` annotation. It also uses Micronaut's validation features to ensure the date format is correct.

```java
@Singleton
public class MoonPhasesMcpServer {
    @Inject
    private MoonPhasesService moonPhasesService;

    @Tool(name = "current-moon-phase",
        description = "Provides the current moon phase")
    public MoonPhase currentMoonPhase() {
        return moonPhasesService.currentMoonPhase();
    }

    @Tool(name = "moon-phase-at-date",
        description = "Provides the moon phase at a certain date (with a format of yyyy-MM-dd)")
    public MoonPhase moonPhaseAtDate(
        @ToolArg(name = "localDate")
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
        String localDate
    ) {
        LocalDate parsedLocalDate = LocalDate.parse(localDate);
        return moonPhasesService.moonPhaseAtUnixTimestamp(parsedLocalDate.toEpochDay() * 86400);
    }
}
```

## Reproducing the Project

### Creating the Micronaut application

This project was bootstrapped with the `mn` Micronaut command-line tool, which can be [installed via SDKman](https://sdkman.io/sdks/micronaut/).

```bash
mn create-app --build=gradle --jdk=21 --lang=java --test=junit \
  --features=jackson-databind,json-schema,validation,json-schema-validation mn.mcp.server.mn-mcp-server
```

### Custom Dependencies

The following dependencies were added to `build.gradle` to support the MCP server and enhance JSON Schema generation:

```groovy
dependencies {
    // The Micronaut MCP support
    implementation("io.micronaut.mcp:micronaut-mcp-server-java-sdk:0.0.3")

    // For rich JSON schema handling
    annotationProcessor("io.micronaut.jsonschema:micronaut-json-schema-processor:1.7.0")
    implementation("io.micronaut.jsonschema:micronaut-json-schema-annotations:1.7.0")
}
```

---

This project is licensed under the [Apache 2 license](LICENSE).

> [!NOTE]
> This is not an official Google project