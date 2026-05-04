# Use a lightweight Java runtime image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the built jar file (the Maven build will produce it)
COPY target/*.jar app.jar

# Expose the port your app listens on (default 8080)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]