FROM openjdk:17
COPY "./target/quiz-1.jarr" "app.jar"
EXPOSE 8110
ENTRYPOINT [ "java", "-jar", "app.jar" ]