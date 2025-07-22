package com.ey.advisory.app.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.ey.advisory.controller.ManageEinvSoapController;
import com.ey.advisory.controller.ManageEinvVersionSoapController;
import com.ey.advisory.controller.ManageEwbSoapController;
import com.ey.advisory.controller.ManageEwbVersionSoapController;
import com.ey.advisory.controller.ManagePayloadSoapController;

import jakarta.xml.ws.Endpoint;

@Configuration
public class CxfConfig {

	private final Bus bus;

	public CxfConfig(Bus bus) {
		this.bus = bus;
	}

	// Register the CXF servlet for SOAP services
	@Bean
	public ServletRegistrationBean<CXFServlet> cxfServlet() {
		return new ServletRegistrationBean<>(new CXFServlet(), "/wsapi/*");
	}

	// Expose the SOAP endpoint
	@Bean
	public Endpoint payloadApiPoint() {
		EndpointImpl endpoint = new EndpointImpl(bus, new ManagePayloadSoapController());
		endpoint.publish("/payloadAPIs");
		return endpoint;
	}

	@Bean
	public EndpointImpl einvApiendPoint() {
		EndpointImpl endpoint = new EndpointImpl(bus, new ManageEinvSoapController());
		endpoint.publish("/einvAPIs");
		return endpoint;
	}

	@Bean
	public EndpointImpl ewbApiendPoint() {
		EndpointImpl endpoint = new EndpointImpl(bus, new ManageEwbSoapController());
		endpoint.publish("/ewbAPIs");
		return endpoint;
	}

	@Bean
	public EndpointImpl ewbVersionApiendPoint() {
		EndpointImpl endpoint = new EndpointImpl(bus, new ManageEwbVersionSoapController());
		endpoint.publish("/v3/ewbAPIs");
		return endpoint;
	}

	@Bean
	public EndpointImpl einvVersionApiPoint() {
		EndpointImpl endpoint = new EndpointImpl(bus, new ManageEinvVersionSoapController());
		endpoint.publish("/v3/einvAPIs");
		return endpoint;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();

	}

}