package mn.mcp.server;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

public class MoonPhaseWithLc4jClientTest {
    @Test
    @EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
    void test() {
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class);

        interface Bot {
            String chat(String msg);
        }

        ChatModel model = GoogleGenAiChatModel.builder()
                .modelName("gemini-2.5-flash")
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();

        McpTransport transport = StreamableHttpMcpTransport.builder()
                .url(embeddedServer.getURI().toString() + "/mcp")
                .build();

        McpClient mcpClient = DefaultMcpClient.builder()
                .key("MoonPhaseClient")
                .transport(transport)
                .build();

        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();

        Bot bot = AiServices.builder(Bot.class)
                .chatModel(model)
                .toolProvider(toolProvider)
                .build();

        String response = bot.chat(
                "What's the current phase of the moon?");

        System.out.println(response);

    }
}
