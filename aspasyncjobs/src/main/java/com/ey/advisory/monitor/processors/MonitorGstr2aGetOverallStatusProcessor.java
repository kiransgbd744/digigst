package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Gstr2aGetOverallStatusHandler;
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
@Service("MonitorGstr2aGetOverallStatusProcessor")
public class MonitorGstr2aGetOverallStatusProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private Gstr2aGetOverallStatusHandler handler;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"GSTR2a GET sections Overall status periodic job  execution "
							+ "started for groupcode {} ",
					group.getGroupCode()));
		}

		handler.execute(group.getGroupCode());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"GSTR2a GET sections Overall status periodic job  execution "
							+ "completed for groupcode {} ",
					group.getGroupCode()));
		}

	}

}
