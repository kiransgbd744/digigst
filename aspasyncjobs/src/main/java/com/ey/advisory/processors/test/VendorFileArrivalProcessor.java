package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.VendorFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Component("VendorFileArrivalProcessor")
public class VendorFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorFileArrivalProcessor.class);

	@Autowired
	@Qualifier("VendorFileArrivalHandler")
	private VendorFileArrivalHandler vendorFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			LOGGER.debug("Vendor File arrived");
			vendorFileArrivalHandler.processVendorFile(message, context);
			LOGGER.debug("Vendor File Arrival Processed");
		} catch (AppException e) {
			LOGGER.error("Exception while processing the Vendor file ",
					e.getMessage());
			throw e;
		}
	}

}
