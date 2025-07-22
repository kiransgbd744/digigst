package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.ComprehensiveInwardSRFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("ComprehensiveInwardFileArrivalProcessor")
@Slf4j
public class ComprehensiveInwardFileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ComprehensiveInwardSRFileArrivalHandler")
	private ComprehensiveInwardSRFileArrivalHandler srFileArrivalHandler;

	public void execute(Message message, AppExecContext context) {
		try {
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"COMPRENSIVE_INWARD_SR_FILE_ARRIVAL_PROCESSOR_START",
					"ComprehensiveInwardSRFileArrivalHandler", "execute", null);
			
			srFileArrivalHandler.processInwardSRFile(message, context);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"COMPRENSIVE_INWARD_SR_FILE_ARRIVAL_PROCESSOR_END",
					"ComprehensiveInwardSRFileArrivalHandler", "execute", null);
		} catch (AppException e) {
			LOGGER.error("Exception while processing the SR file ", e);
			throw e;
		}
	}
}
