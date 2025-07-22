package com.ey.advisory.app.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ey.advisory.web.filter.APIRequestGroupResolverFilter;

import jakarta.servlet.Filter;


@Configuration
public class FilterConfiguration {

	@Bean
	public FilterRegistrationBean<Filter> authFilter() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new APIRequestGroupResolverFilter());
		registrationBean.addUrlPatterns("/api/*"); 
		registrationBean.setName("APIRequestGroupResolverFilter");
		registrationBean.setOrder(1); 
		return registrationBean;
	}

	
	/*
	 * @Bean public FilterRegistrationBean<Filter> groupResolverFilter() {
	 * FilterRegistrationBean<Filter> registrationBean = new
	 * FilterRegistrationBean<>(); registrationBean.setFilter(new
	 * GroupResolverFilter()); registrationBean.addUrlPatterns("*.do");
	 * registrationBean.setName("GroupResolverFilter");
	 * registrationBean.setOrder(2); return registrationBean; }
	 */
}
