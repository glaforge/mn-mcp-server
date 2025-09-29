/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mn.mcp.server;

import io.micronaut.jsonschema.JsonSchema;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @param phase The Moon Phase
 * @param emoji An emoji to illustrate the Moon Phase
 */
@JsonSchema(
    title = "Phase of the moon",
    description = "The phase of the moon is composed of the name of the phase and an emoji representing it",
    uri = "/moonPhase"
)
@Serdeable
public record MoonPhaseEmoji(
    MoonPhase phase,
    @NotBlank String emoji
) { }
