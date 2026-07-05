package mn.mcp.server;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

class AnswerMoonPhaseTest {
    @Test
    void test() {
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport
            .builder("http://localhost:8080/mcp")
            .build();

        McpSyncClient client = McpClient.sync(transport)
            .requestTimeout(Duration.ofSeconds(10))
            .build();

        client.initialize();

        McpSchema.CallToolResult result = client.callTool(
            new McpSchema.CallToolRequest("current-moon-phase", Map.of())
        );

        System.out.println("THE_ANSWER_IS: " + result.content().getFirst().toString());
        client.closeGracefully();
    }
}
