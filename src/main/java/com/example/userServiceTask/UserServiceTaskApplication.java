package com.example.userServiceTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserServiceTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceTaskApplication.class, args);
	}

}
