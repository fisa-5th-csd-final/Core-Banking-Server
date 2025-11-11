FROM gradle:8.10-jdk17 AS builder
WORKDIR /workspace
COPY bank ./bank
WORKDIR /workspace/bank
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/bank/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
