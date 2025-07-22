package com.ey.advisory.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ey.advisory.filter.UIRequestGroupResolverFilter;
import com.ey.advisory.filter.UserLoaderFilter;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {
	Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	@Qualifier("XsuaaConfiguration")
	private XsuaaServiceConfiguration xsuaaServiceConfiguration;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    LOGGER.error("Entered to configure Method, {}", http);

	    http.sessionManagement(sessionManagement -> 
	            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(authorizeRequests -> 
	            authorizeRequests.anyRequest().permitAll())
	        .csrf().disable();

	    http.addFilterBefore(new UIRequestGroupResolverFilter(), UsernamePasswordAuthenticationFilter.class);
	    http.addFilterBefore(new UserLoaderFilter(), UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}


	TokenAuthenticationConverter getJwtAuthenticationConverter() {
		TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
		converter.setLocalScopeAsAuthorities(true);
		return converter;
	}

}
