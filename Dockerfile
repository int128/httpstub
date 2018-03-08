FROM openjdk:8-jdk-alpine

ADD . /app

RUN /app/gradlew build --build-file /app/build.gradle

EXPOSE 8080

WORKDIR /app
CMD ["/usr/bin/java", "-jar", "./build/libs/httpstub.jar"]