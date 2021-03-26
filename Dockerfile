FROM openjdk:11-jdk-slim AS builder

WORKDIR /builder/httpstub
COPY *.gradle gradlew .
COPY gradle/ gradle/
RUN ./gradlew --version

COPY src/ src/
RUN ./gradlew build --no-daemon build -x test

FROM openjdk:11-jre-slim

WORKDIR /app
COPY --from=builder /builder/httpstub/build/libs/httpstub.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "httpstub.jar"]
