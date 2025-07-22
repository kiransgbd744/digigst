package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.recon.jobs.anx2.Anx2ReconciliationPr;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("Anx2ReconPrProcessor")
public class Anx2ReconPrProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2ReconPrProcessor.class);

	@Autowired
	private Anx2ReconciliationPr doc;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		LOGGER.debug("Anx2Recon PR Data Execute method ON with "
				+ "groupcode {}", groupCode);
		if (groupCode != null) {
			 doc.callPrProc(jsonString, groupCode);
			LOGGER.info("Anx2Recon PR Processed with args {} and groupCode {}",
					jsonString, groupCode);
		}

	}
}
