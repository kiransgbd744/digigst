package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.MasterCustomerFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MasterCustomerFileArrivalProcessor")
public class MasterCustomerFileArrivalProcessor  implements TaskProcessor  {
	@Autowired
	@Qualifier("MasterCustomerFileArrivalHandler")
	private MasterCustomerFileArrivalHandler masterCustomerFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("Enter into MasterCustomerFileArrivalProcessor" );	
		masterCustomerFileArrivalHandler.processCusterData(message, context);
		LOGGER.debug("Ended With MasterCustomerFileArrivalProcessor" );	
		
	}
}
