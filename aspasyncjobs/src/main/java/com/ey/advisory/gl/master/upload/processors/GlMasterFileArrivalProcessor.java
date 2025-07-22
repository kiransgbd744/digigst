package com.ey.advisory.gl.master.upload.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.einvoice.ImsFileArrivalHandler;
import com.ey.advisory.app.services.gl.masterFile.uploads.GlMasterFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Kiran s
 *
 */

@Component("GlMasterFileArrivalProcessor")
public class GlMasterFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GlMasterFileArrivalProcessor.class);

	@Autowired
	@Qualifier("GlMasterFileArrivalHandler")
	private GlMasterFileArrivalHandler glMasterFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Ims file arrived");
			}

			glMasterFileArrivalHandler.processGlMasterFile(message, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Ims file arrival processed");
			}

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Ims file ",
					e.getMessage());
			throw e;
		}
	}
}
