/**
 * 
 */
package com.ey.advisory.sap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.processors.handler.RevIntegrationScenarioTriggerTaskHandler;

/**
 * @author Hemasundar.J
 *
 */
@RestController
public class RevIntegrationScenarioTriggerTaskTestController {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(RevIntegrationScenarioTriggerTaskTestController.class);
		
		@Autowired
		private RevIntegrationScenarioTriggerTaskHandler handler;
		
		@PostMapping(value = "/scenarioTrigger", produces = {
				MediaType.APPLICATION_JSON_VALUE })
		public void scenarioTrigger() {
			String groupcode = TestController.staticTenantId();
			
			if (groupcode != null) {
				LOGGER.debug("Rev Integration Scenario trigger executeForGroup "
						+ "method is ON with groupcode {}", groupcode);
				handler.triggerCronEligibleJobs(groupcode, null);
			}
		}

	}

