package com.lunch.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LunchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunchBackendApplication.class, args);
	}

}
