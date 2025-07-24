# ---- Stage 1: Build the application ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only the pom.xml and download dependencies (for caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the app port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
