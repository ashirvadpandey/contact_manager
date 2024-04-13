FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /app/myapp.jar

EXPOSE 8080
CMD ["java","-jar","myaap.jar"]
