FROM openjdk:22-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN chmod +x ./gradlew && ./gradlew bootJar -x test

FROM openjdk:22-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
