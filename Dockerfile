# Step 1: Use Maven image to build the app
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the pom.xml and download dependencies first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Step 2: Use a smaller JRE image to run the app
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the Spring Boot port
EXPOSE 8983

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
