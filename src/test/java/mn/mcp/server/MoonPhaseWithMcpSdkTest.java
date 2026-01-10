package mn.mcp.server;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MoonPhaseWithMcpSdkTest {
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class);

    @Test
    void testLocalServer() {
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport
            .builder(embeddedServer.getURI().toString() + "/mcp")
            .build();

        McpSyncClient client = McpClient.sync(transport)
            .requestTimeout(Duration.ofSeconds(10))
            .loggingConsumer(System.out::println)
            .progressConsumer(System.out::println)
            .build();

        client.initialize();

        McpSchema.ListToolsResult tools = client.listTools();
        tools.tools().forEach(System.out::println);

        McpSchema.CallToolResult result = client.callTool(
            new McpSchema.CallToolRequest("moon-phase-at-date", Map.of("date", "2025-10-01"))
        );

        assertTrue(result.content().getFirst().toString().contains("FIRST_QUARTER"));

        client.closeGracefully();
    }
}
