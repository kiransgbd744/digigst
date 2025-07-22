package com.ey.advisory.gstnapi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIReqParts;
import com.ey.advisory.core.api.impl.APIResponseHandler;
import com.ey.advisory.core.api.impl.SaveBatchReqAndRespPayloadDumpHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@Component("GSTNAPIFailureResponseHandler")
public class GSTNAPIFailureResponseHandler implements APIResponseHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(
			GSTNAPIFailureResponseHandler.class);
	
	@Autowired
	private SaveBatchReqAndRespPayloadDumpHelper saveBatchHelper;
	
	@Override
	public APIResponse handleResponse(
					APIParams params, 
					APIConfig config, 
					APIExecParties parties, 
					APIReqParts reqParts,
					APIAuthInfo authInfo, 
					String response,
					Map<String, Object> context) {

		JsonParser jsonParser = new JsonParser();
		
		// Get the JSON object. If GSTN returns an invalid Json, throw
		// an exception.
		JsonObject jsonObject = null;
		try {
			jsonObject = (JsonObject) jsonParser.parse(response);
		} catch (JsonParseException ex) {
			String msg = "GSTN returned Invalid JSON as Response";
			LOGGER.error(msg, ex);
			throw new InvalidAPIResponseException(msg);
		}
		
		// For all error responses, there will be an associated error 
		// object. Get this object, transform to the APIError java object and
		// add it to APIResponse.
		String errorJson = jsonObject.get("error").toString();
		
		//Storing the SaveToGstn and Return status api falure response 
		//Jsons in batch table.
		saveBatchHelper.dumpRespJsonPayload(params, errorJson);
		
		Gson gson = JsonUtil.newGsonInstance();
		APIError error = gson.fromJson(errorJson, APIError.class);
		
		// Create an APIResponse object, attach the error to it and return 
		APIResponse apiResp = new APIResponse();
		apiResp.addError(error);		
		
		return apiResp; // The final response passed to the caller.
	}

}
