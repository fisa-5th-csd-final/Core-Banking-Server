FROM gradle:8.10-jdk17 AS builder
WORKDIR /workspace/bank

# 의존성 관련 파일 복사 및 설치
COPY bank/build.gradle bank/gradlew ./
COPY bank/gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
# 소스 코드 복사
COPY bank/src ./src
# 애플리케이션 빌드
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/bank/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
