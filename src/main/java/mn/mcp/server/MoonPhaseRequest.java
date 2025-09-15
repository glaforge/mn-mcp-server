package mn.mcp.server;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.jsonschema.JsonSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonSchema
@Introspected
public record MoonPhaseRequest(
    @NotNull
    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Input date format: yyyy-MM-dd")
    String localDate
) {
}
