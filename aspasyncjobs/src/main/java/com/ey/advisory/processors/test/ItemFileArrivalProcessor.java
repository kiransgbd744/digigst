package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.ItemFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("ItemFileArrivalProcessor")
public class ItemFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemFileArrivalProcessor.class);

	@Autowired
	@Qualifier("ItemFileArrivalHandler")
	private ItemFileArrivalHandler itemFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Item file arrived");
			itemFileArrivalHandler.processItemFile(message, context);
			LOGGER.debug("Item file arrival processed");
		} catch (AppException e) {
			LOGGER.error("Exception while processing the Item file ",
					e.getMessage());
			throw e;
		}
	}
}
