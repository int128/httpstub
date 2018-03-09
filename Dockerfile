# Build App
FROM openjdk:8-jdk-alpine AS BUILD

ADD . /work
WORKDIR /work
RUN ./gradlew build --build-file ./build.gradle

# Run App
FROM openjdk:8-jre-alpine

COPY --from=BUILD /work/build/libs/httpstub.jar /app/httpstub.jar
EXPOSE 8080
WORKDIR /app
ENTRYPOINT /usr/bin/java -jar httpstub.jar