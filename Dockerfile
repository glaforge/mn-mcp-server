#
# Copyright 2025 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# --- Stage 1: Build ---
# Use a full JDK image to build the application

FROM eclipse-temurin:21 AS builder
WORKDIR /app

# Copy only the necessary files for building to optimize caching
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY src ./src

# Build the shadow JAR. The --no-daemon flag is recommended for CI/CD environments.
RUN ./gradlew shadowJar --no-daemon

# --- Stage 2: Run ---
# Use a lightweight JRE image for the final container

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/build/libs/mn-mcp-server-0.1-all.jar .

EXPOSE 8080

# Run the application
CMD ["java", "-jar", "mn-mcp-server-0.1-all.jar"]
