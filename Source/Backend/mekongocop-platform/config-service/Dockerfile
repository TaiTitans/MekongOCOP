# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application's jar file to the container
COPY target/config-service-1.0.0.jar config-service.jar

# Make port 8081 available to the world outside this container
EXPOSE 8888

# Run the application
ENTRYPOINT ["java", "-jar", "config-service.jar"]
