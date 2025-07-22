package com.ey.advisory.core.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.JsonObject;

/**
 * Stub implementations for all GSTN APIs. Not yet implemented.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("DefaultGSTNStubExecutor")
public class DefaultStubExecutor implements APIExecutor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultStubExecutor.class);

	/**
	 * Currently stubs are not enabled for GSTN API. This method throws an
	 * exception and terminates.
	 */
	@Override
	public APIResponse execute(APIParams params, String reqData) {
		String api = params.getApiIdentifier();
		APIResponse resp = new APIResponse();
		JsonObject respJson = new JsonObject();
		if (api.equals(APIIdentifiers.GSTR1_SAVE)) {
			respJson.addProperty("reference_id",
					"stub-50f7-41d4-8598-hardcoded-resp");
		} else {
			String msg = "Stubs are not yet supported for Other Apis!!";
			LOGGER.error(msg);
			throw new UnsupportedOperationException(msg);
		}
		resp.setResponse(respJson.toString());
		return resp;
	}
}
