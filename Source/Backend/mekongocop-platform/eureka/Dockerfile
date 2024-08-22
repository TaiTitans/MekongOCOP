FROM openjdk:21-jdk-slim


WORKDIR /app


COPY target/eureka-1.0.0.jar /app/eureka.jar


EXPOSE 8761


ENTRYPOINT ["java", "-jar", "/app/eureka.jar"]
