/**
 * 
 */
package com.ey.advisory.monitor.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.processors.handler.RevIntegrationScenarioTriggerTaskHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

/**
 * @author Hemasundar.J
 *
 */
@Component("RevIntegrationScenarioTriggerTaskProcessor")
public class RevIntegrationScenarioTriggerTaskProcessor
		extends DefaultMultiTenantTaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RevIntegrationScenarioTriggerTaskProcessor.class);
	
	@Autowired
	private RevIntegrationScenarioTriggerTaskHandler handler;
	
	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		String groupcode = group.getGroupCode();
		if (groupcode != null) {
			LOGGER.debug("Rev Integration Scenario trigger executeForGroup "
					+ "method is ON with groupcode {}", groupcode);
			handler.triggerCronEligibleJobs(groupcode, message);
		}
	}

}
