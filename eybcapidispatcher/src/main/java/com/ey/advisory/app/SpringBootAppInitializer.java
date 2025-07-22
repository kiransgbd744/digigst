package com.ey.advisory.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.ey.advisory")
@SpringBootApplication
public class SpringBootAppInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAppInitializer.class, args);
	}

}
	