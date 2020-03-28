# Build App
FROM openjdk:11-jdk-slim AS BUILD

COPY *.gradle gradle.* gradlew /work/
COPY gradle /work/gradle
WORKDIR /work
RUN ./gradlew --version

COPY . /work
RUN ./gradlew build --build-file ./build.gradle

# Run App
FROM openjdk:11-jre-slim

COPY --from=BUILD /work/build/libs/httpstub.jar /app/httpstub.jar
EXPOSE 8080
WORKDIR /app
ENTRYPOINT ["java", "-jar", "httpstub.jar"]