package mn.mcp.server;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.jsonschema.JsonSchema;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 *
 * @param date the date in format yyyy-MM-dd
 */
@JsonSchema
@Serdeable
public record MoonPhaseRequest(@NonNull @NotNull LocalDate date) {
}
