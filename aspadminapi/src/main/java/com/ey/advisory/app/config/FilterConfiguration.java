package com.ey.advisory.app.config;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ey.advisory.filter.GroupResolverFilter;
import com.ey.advisory.filter.UIRequestGroupResolverFilter;
import com.ey.advisory.filter.UserLoaderFilter;

import jakarta.servlet.Filter;

@Configuration
public class FilterConfiguration {

	@Bean
	public FilterRegistrationBean<Filter> groupResolverFilter() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new UIRequestGroupResolverFilter());
		registrationBean.addUrlPatterns("*.do"); 
		registrationBean.setName("UIRequestGroupResolverFilter");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<Filter> userLoaderFilter() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new UserLoaderFilter());
		registrationBean.addUrlPatterns("*.do"); 
		registrationBean.setName("UserLoaderFilter");
		registrationBean.setOrder(2);
		return registrationBean;
	}

}
