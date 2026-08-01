package com.jobboard.jobboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobboardApplication {
	public static void main(String[] args) {
		SpringApplication.run(JobboardApplication.class, args);
	}
}
