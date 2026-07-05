package mn.mcp.server;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.agents.RunConfig;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.StreamableHttpServerParameters;

import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

class MoonPhaseWithAdkTest {
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class);

    @Test
    @EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
    void testLocalServer() {
        StreamableHttpServerParameters params = StreamableHttpServerParameters
            .builder()
            .url(embeddedServer.getURI().toString() + "/mcp")
            .build();

        try (McpToolset mcpToolset = new McpToolset(params)) {
            List<BaseTool> moonPhasesTools =
                mcpToolset.getTools(null).toList().blockingGet();

            LlmAgent moonExpertAgent = LlmAgent.builder()
                .name("moon-expert")
                .model("gemini-3.5-flash")
                .description("a moon expert")
                .instruction("""
                    You are a knowledgeable astronomy expert
                    focusing on everything about the moon.
                    """)
                .tools(moonPhasesTools)
                .build();

            List<Event> allEvents = runLoop(moonExpertAgent,
                Content.fromParts(Part.fromText("What is the phase of the moon on October 1st of 2025?")));

            System.out.println("allEvents = " + allEvents);
        }
    }

    private static List<Event> runLoop(BaseAgent agent, Object... messages) {
        ArrayList<Event> allEvents = new ArrayList<>();

        Runner runner = new InMemoryRunner(agent, agent.name());
        Session session = runner.sessionService().createSession(agent.name(), "user132").blockingGet();

        for (Object message : messages) {
            Content messageContent = null;
            if (message instanceof String) {
                messageContent = Content.fromParts(Part.fromText((String) message));
            } else if (message instanceof Part) {
                messageContent = Content.fromParts((Part) message);
            } else if (message instanceof Content) {
                messageContent = (Content) message;
            }
            allEvents.addAll(
                runner.runAsync(session.sessionKey(), messageContent, RunConfig.builder().build())
                    .blockingStream()
                    .toList()
            );
        }

        return allEvents;
    }
}
