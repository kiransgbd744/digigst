/**
 * 
 */
package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr6SaveOverallStatusHandler;
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
@Service("MonitorGstr6SaveOverallStatusProcessor")
public class MonitorGstr6SaveOverallStatusProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private Gstr6SaveOverallStatusHandler handler;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"GSTR6 SAVE sections Overall status periodic job  execution "
							+ "started for groupcode {} ",
					group.getGroupCode()));
		}
		String userName = message.getUserName();
		handler.execute(group.getGroupCode(), userName);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"GSTR6 SAVE sections Overall status periodic job  execution "
							+ "completed for groupcode {} ",
					group.getGroupCode()));
		}

	}

}
