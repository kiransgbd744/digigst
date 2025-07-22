/**
 * 
 */
package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.MonitorPayloadIdRevIntegrationRetryHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("MonitorPayloadIdRevIntegrationRetryProcessor")
public class MonitorPayloadIdRevIntegrationRetryProcessor
		extends DefaultMultiTenantTaskProcessor {

	
	@Autowired
	private MonitorPayloadIdRevIntegrationRetryHandler handler ;
	
	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"PayloadId Based Reverse Integration status periodic job execution "
							+ "started for groupcode {} ",
					group.getGroupCode()));
		}

		handler.execute(group.getGroupCode());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"PayloadId Based Reverse Integration status periodic job execution "
							+ "completed for groupcode {} ",
					group.getGroupCode()));
		}

	}
	
}
