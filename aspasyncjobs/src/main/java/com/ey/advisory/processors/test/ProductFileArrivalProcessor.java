package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.ProductFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("ProductFileArrivalProcessor")
public class ProductFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProductFileArrivalProcessor.class);

	@Autowired
	@Qualifier("ProductFileArrivalHandler")
	private ProductFileArrivalHandler productFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			LOGGER.debug("Product file arrived");
			productFileArrivalHandler.processProductFile(message, context);
			LOGGER.debug("Product file arrival processed");

		} catch (AppException e) {
			LOGGER.error("Exception while processing the Product file ",
					e.getMessage());
			throw e;
		}
	}
}
