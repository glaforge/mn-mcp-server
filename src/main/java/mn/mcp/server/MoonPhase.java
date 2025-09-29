package mn.mcp.server;

/**
 * Enumeration representing the eight phases of the Moon during its lunar cycle.
 * Each phase includes a description.
 */
public enum MoonPhase {
    NEW_MOON("New Moon"),
    WAXING_CRESCENT("Waxing Crescent"),
    FIRST_QUARTER("First Quarter"),
    WAXING_GIBBOUS("Waxing Gibbous"),
    FULL_MOON("Full Moon"),
    WANING_GIBBOUS("Waning Gibbous"),
    LAST_QUARTER("Last Quarter"),
    WANING_CRESCENT("Waning Crescent");

    private final String displayName;

    /**
     * Constructor for MoonPhase enum.
     *
     * @param displayName The human-readable name of the phase
     */
    MoonPhase(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the human-readable display name of the moon phase.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}