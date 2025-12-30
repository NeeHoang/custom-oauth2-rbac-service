package com.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticationApplication {

	public static void main(String[] args) {
//		System.out.println("ENV APP_JWT_SECRET = " + System.getProperty("APP_JWT_SECRET"));
		SpringApplication.run(AuthenticationApplication.class, args);
	}
}
