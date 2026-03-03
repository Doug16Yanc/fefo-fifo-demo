FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

RUN groupadd --gid 1001 builder && \
    useradd --uid 1001 --gid 1001 --shell /bin/bash --create-home builder && \
    chown -R builder:builder /app

USER builder

COPY --chown=builder:builder gradlew .
COPY --chown=builder:builder gradle ./gradle
COPY --chown=builder:builder build.gradle .
COPY --chown=builder:builder settings.gradle .

RUN chmod +x gradlew && \
    ./gradlew dependencies --no-daemon

COPY --chown=builder:builder src ./src

RUN ./gradlew bootJar -x test --no-daemon && \
    mv build/libs/*.jar build/libs/app.jar

FROM eclipse-temurin:25-jre AS runtime

COPY --from=build --chown=nonroot:nonroot /app/build/libs/app.jar /app/app.jar

WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]