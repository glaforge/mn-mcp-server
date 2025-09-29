package mn.mcp.server;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MoonPhasesServiceTestEmoji {

    @Test
    void moonPhaseAtSergioBirthday(MoonPhasesService service) {
        MoonPhaseEmoji moonPhaseEmoji = service.moonPhaseAtDate(LocalDate.of(1982, 10, 28));
        assertEquals(MoonPhase.WAXING_GIBBOUS, moonPhaseEmoji.phase());
    }

    @Test
    void moonPhase(MoonPhasesService service) {
        long timestamp = LocalDate.of(1982, 10, 28).toEpochDay() * (24 * 60 * 60);
        MoonPhaseEmoji moonPhaseEmoji = service.moonPhaseAtUnixTimestamp(timestamp);
        assertEquals(MoonPhase.WAXING_GIBBOUS, moonPhaseEmoji.phase());
    }

    @Test
    void moonPhaseToday(MoonPhasesService service) {
        assertDoesNotThrow(service::currentMoonPhase);
    }
}