package mn.mcp.server;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

public class MoonPhaseWithLc4jClientTest {
    @Test
    void test() {
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class);

        interface Bot {
            String chat(String msg);
        }

        ChatModel model = GoogleAiGeminiChatModel.builder()
                .modelName("gemini-2.5-flash")
                .apiKey(System.getenv("GOOGLE_API_KEY"))
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
