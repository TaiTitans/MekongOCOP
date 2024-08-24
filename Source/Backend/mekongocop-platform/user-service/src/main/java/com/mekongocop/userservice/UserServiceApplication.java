package com.mekongocop.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();
		// Set environment variables
		System.setProperty("jwt.secret", dotenv.get("TOKEN"));
		System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
		System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
		System.setProperty("MAIL_API", dotenv.get("MAIL_API"));
		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		System.setProperty("REDIS_HOST", dotenv.get("REDIS_HOST"));
		System.setProperty("REDIS_PORT", dotenv.get("REDIS_PORT"));
		System.setProperty("REDIS_PASSWORD", dotenv.get("REDIS_PASSWORD"));
		System.setProperty("CLOUDINARY_URL", dotenv.get("CLOUDINARY_URL"));

		SpringApplication.run(UserServiceApplication.class, args);
	}

}
