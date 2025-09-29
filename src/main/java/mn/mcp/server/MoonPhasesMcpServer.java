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

import jakarta.inject.Singleton;
import io.micronaut.mcp.annotations.Tool;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Singleton
class MoonPhasesMcpServer {
    private final MoonPhasesService moonPhasesService;

    MoonPhasesMcpServer(MoonPhasesService moonPhasesService) {
        this.moonPhasesService = moonPhasesService;
    }

    @Tool(name = "current-moon-phase",
        description = "Provides the current moon phase")
    MoonPhaseEmoji currentMoonPhase() {
        return moonPhasesService.currentMoonPhase();
    }

    @Tool(name = "moon-phase-at-date",
            description = "Provides the moon phase at a certain date (with a format of yyyy-MM-dd)")
    @NotNull
    MoonPhaseEmoji moonPhaseAtDate(@Valid MoonPhaseRequest moonPhaseRequest) {
        return moonPhasesService.moonPhaseAtDate(moonPhaseRequest.date());
    }
}
