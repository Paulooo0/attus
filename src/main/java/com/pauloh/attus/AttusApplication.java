package com.pauloh.attus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AttusApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttusApplication.class, args);
	}

}
