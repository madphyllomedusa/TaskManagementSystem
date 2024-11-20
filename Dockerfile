# Use an official Java runtime as a parent image
FROM eclipse-temurin:21

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/TaskManagementSystem-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
