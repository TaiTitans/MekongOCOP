package com.mekongocop.authservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

	public static void main(String[] args) {
//		// Load .env file
//		Dotenv dotenv = Dotenv.configure().load();
//		// Set environment variables
//		System.setProperty("DB_URL", dotenv.get("DB_URL"));
//		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
//		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
//		System.setProperty("jwt.secret", dotenv.get("TOKEN"));
////		System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
//		System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
//		System.setProperty("MAIL_API", dotenv.get("MAIL_API"));
//		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
//		System.setProperty("REDIS_HOST", dotenv.get("REDIS_HOST"));
//		System.setProperty("REDIS_PORT", dotenv.get("REDIS_PORT"));
//		System.setProperty("REDIS_PASSWORD", dotenv.get("REDIS_PASSWORD"));

		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
