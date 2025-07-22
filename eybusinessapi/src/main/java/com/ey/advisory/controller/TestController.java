/**
 * 
 */
package com.ey.advisory.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Slf4j
@RestController
public class TestController {
	
	@PostMapping(value = "/api/testActuator", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String testActuator() {
		
		LOGGER.debug(" Hi this is debug log");
		LOGGER.error(" Hi , This is error log");
		LOGGER.warn(" Hi this is warn log");
		LOGGER.info(" Hi this is info log");
		
		return "api has been called";
		
	}
	
	@PostMapping(value = "/ui/testActuatorUi", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String testActuatorUi() {
		
		LOGGER.debug(" Hi this is debug log");
		LOGGER.error(" Hi , This is error log");
		LOGGER.warn(" Hi this is warn log");
		LOGGER.info(" Hi this is info log");
		
		return "api has been called";
		
	}

}
