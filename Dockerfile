FROM eclipse-temurin:17-jdk-alpine
COPY "./target/quiz-1.jarr" "app.jar"
EXPOSE 8110
ENTRYPOINT [ "java", "-jar", "app.jar" ]
