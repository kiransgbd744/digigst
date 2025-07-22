/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.services.einvoice.EinvoiceAsyncService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Service("GenerateIrnProcessor")
@Slf4j
public class GenerateIrnProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("EinvoiceAsyncServiceImpl")
	private EinvoiceAsyncService einvoiceAsyncService;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.info("GenerateIrnProcessor Begin");
			}
			String jsonString = message.getParamsJson();
			JsonObject requestParams = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Long id =  requestParams.get("id").getAsLong();
			String isEwbRequired = requestParams.get("isEwbRequired").getAsString();
			if(LOGGER.isDebugEnabled()) {
				LOGGER.info("GenerateIrnProcessor ,calling "
						+ "einvoiceAsyncService with Id " + id);
			}
			einvoiceAsyncService.generateIrn(id, isEwbRequired);
			if(LOGGER.isDebugEnabled()) {
				LOGGER.info("GenerateIrnProcessor Job Completed");
			}	
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			throw new AppException(e);
		}
		
	}

}
