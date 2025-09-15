FROM eclipse-temurin:21
WORKDIR /app
COPY ./ ./
RUN ./gradlew shadowJar
EXPOSE  8080
CMD ["java", "-jar", "build/libs/mn-mcp-server-0.1-all.jar"]