package com.ey.advisory.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.ey.advisory")
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpringBootAppInitializer {

	public static void main(String[] args) {
		SpringApplication.run(new Class[] { SpringBootAppInitializer.class },
				args);
	}

}
