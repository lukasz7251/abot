package com.labreh.abot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AbotApplication {
	public static void main(String[] args) {
		SpringApplication.run(AbotApplication.class, args);
	}
}
