FROM openjdk:21-jdk-slim


WORKDIR /app


COPY target/api-gateway-1.0.0.jar /app/api-gateway.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]