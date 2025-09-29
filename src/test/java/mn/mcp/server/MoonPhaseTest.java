package mn.mcp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoonPhaseTest {
    @Test
    void getters_returnExpectedValues() {
        MoonPhase full = MoonPhase.FULL_MOON;
        assertEquals("Full Moon", full.getDisplayName());

        MoonPhase firstQuarter = MoonPhase.FIRST_QUARTER;
        assertEquals("First Quarter", firstQuarter.getDisplayName());
    }

    @Test
    void toString_returnsDisplayNameOnly() {
        assertEquals("Full Moon", MoonPhase.FULL_MOON.toString());
        assertEquals("New Moon", MoonPhase.NEW_MOON.toString());
    }
}
