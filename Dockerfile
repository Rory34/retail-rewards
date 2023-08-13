FROM openjdk:19-slim
COPY build/libs/*.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar","/app.jar"]