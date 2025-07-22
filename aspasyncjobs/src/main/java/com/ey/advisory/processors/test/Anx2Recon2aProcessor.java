package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.recon.jobs.anx2.Anx2Reconciliation2a;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("Anx2Recon2aProcessor")
public class Anx2Recon2aProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2Recon2aProcessor.class);

	@Autowired
	private Anx2Reconciliation2a doc;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		LOGGER.debug("Anx2Recon 2a Data Execute method is ON with "
				+ "groupcode {} and params {}", groupCode, jsonString);
		if (jsonString != null && groupCode != null) {
			doc.call2aProc(jsonString, groupCode);
			LOGGER.info("Anx2Recon 2a Processed with args {} ", jsonString);
		}

	}

}
