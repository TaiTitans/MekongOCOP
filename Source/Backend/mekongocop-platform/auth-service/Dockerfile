# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application's jar file to the container
COPY target/auth-service-1.0.0.jar auth-service.jar

# Make port 8081 available to the world outside this container
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
# Run the application with the Docker profile
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "auth-service.jar"]
# Add this line before the ENTRYPOINT
ENV SPRING_PROFILES_ACTIVE=docker