package mn.mcp.server;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MoonPhasesMcpServerTest {

    @Test
    void moonPhaseAtSergioBirthday(MoonPhasesMcpServer server) {
        MoonPhaseEmoji moonPhaseEmoji = server.moonPhaseAtDate(new MoonPhaseRequest(LocalDate.of(1982, 10, 28)));
        assertEquals(MoonPhase.WAXING_GIBBOUS, moonPhaseEmoji.phase());
    }

    @Test
    void currentMoonPhase(MoonPhasesMcpServer server) {
        assertDoesNotThrow(server::currentMoonPhase);
    }
}