package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Itc04Table5SaveToGstnJobHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Service("MonitorItc04Table5PollingReadinessProcessor")
public class MonitorItc04Table5PollingReadinessProcessor 
 					extends DefaultMultiTenantTaskProcessor{
	
	@Autowired
	@Qualifier("Itc04Table5SaveToGstnJobHandler")
	private Itc04Table5SaveToGstnJobHandler handler;

	@Override
	public void executeForGroup(Group group, Message message, AppExecContext ctx) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"ITC04 TABLE5 SAVE sections status periodic job  execution "
							+ "started for groupcode {} ",
					group.getGroupCode()));
		}
		String userName = message.getUserName();
		handler.execute(group.getGroupCode(), userName);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"ITC04 TABLE5 SAVE sections status periodic job  execution "
							+ "completed for groupcode {} ",
					group.getGroupCode()));
		}
		
	}

}
