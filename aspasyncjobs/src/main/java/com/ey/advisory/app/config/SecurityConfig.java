package com.ey.advisory.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {
	Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		LOGGER.error("Entered to configure Method, {}", http);

		http.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/api/**").permitAll()
						.requestMatchers("/ui/**").permitAll().requestMatchers("/actuator/**").permitAll().requestMatchers("/executeAsyncJob.do").permitAll())
						 .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/ui/**", "/actuator/**", "/executeAsyncJob.do"));

						return http.build();
	}
}