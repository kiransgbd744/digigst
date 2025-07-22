package com.ey.advisory.app.initializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
	
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ComponentScan("com.ey.advisory")
@SpringBootApplication
public class SpringBootAppInitializer {
		
	public static void main(String[] args) {	
		SpringApplication.run(SpringBootAppInitializer.class, args);
	}
}
