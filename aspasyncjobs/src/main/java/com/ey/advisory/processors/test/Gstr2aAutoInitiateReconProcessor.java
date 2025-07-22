package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.Gstr2aAutoInitiateReconService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("Gstr2aAutoInitiateReconProcessor")
public class Gstr2aAutoInitiateReconProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2aAutoInitiateReconServiceImpl")
	private Gstr2aAutoInitiateReconService autoInitiateReconService;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String jsonString = message.getParamsJson();
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);

			String gstin = json.get("gstin").getAsString();
			Long entityId = json.get("entityId").getAsLong();
			Long autoReconId = json.get("id").getAsLong();
			Long configId = json.get("configId").getAsLong();
			String retType = json.get("returnType").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Gstr2aAutoInitiateReconProcessor with gstin :%s"
								+ "entityId :%s and configId :%s",
						gstin, entityId, configId);
				LOGGER.debug(msg);
			}
			autoInitiateReconService.initiateAutoRecon(gstin, entityId,
					autoReconId, configId, retType);

		} catch (Exception ee) {
			String msg = "Exception occurred while auto initiating recon after"
					+ " auto Get Gstr2a";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

}
