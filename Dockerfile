FROM amazoncorretto:17
WORKDIR /opt
COPY /target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]