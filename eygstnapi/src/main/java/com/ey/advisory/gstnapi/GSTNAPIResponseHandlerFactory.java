package com.ey.advisory.gstnapi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIReqParts;
import com.ey.advisory.core.api.impl.APIResponseHandler;
import com.ey.advisory.core.api.impl.APIResponseHandlerFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * This class examines the status code of GSTN response and determines if a
 * success or failure is encountered. This class also checks if the JSON
 * returned by GSTN is valid. If a status_cd element is not present in the
 * response JSON from GSTN, then it considers this as an exceptional condition
 * and throws an API Response complaining about an invalid response. If the
 * status_cd is "1", then it considers the response as success (which is the
 * GSTN convention) and it routes the response to the SuccessResponseHandler for
 * further processing. If the response is "0", it routes it to the
 * FailureResponseHandler for further processing.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("GSTNAPIResponseHandlerFactory")
public class GSTNAPIResponseHandlerFactory
		implements APIResponseHandlerFactory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSTNAPIResponseHandlerFactory.class);

	@Autowired
	@Qualifier("GSTNAPISuccessResponseHandler")
	private APIResponseHandler successHandler;

	@Autowired
	@Qualifier("GSTNAPIFailureResponseHandler")
	private APIResponseHandler failureHandler;

	@Override
	public APIResponseHandler getHandler(APIParams params, APIConfig config,
			APIExecParties parties, APIReqParts reqParts, APIAuthInfo authInfo,
			String response, Map<String, Object> context) {

		// TODO: Use a streaming parser to extract the status code alone.
		// Since, some of the responses of the API are huge, it makes sense
		// not to load the entire JSON into memory, just for determining the
		// status code. Hence we need to make this a streaming parser.

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

		if (!jsonObject.has("status_cd")) {
			String msg = String.format(
					"GSTN returned Invalid JSON as Response %s", response);
			LOGGER.error(msg);
			throw new InvalidAPIResponseException(msg);
		}

		String statusCode = jsonObject.get("status_cd").getAsString();

		if (statusCode == null || (!statusCode.trim().equals("1")
				&& !statusCode.trim().equals("0")
				&& !statusCode.trim().equals("2")
				&& !statusCode.trim().equals("3"))) {
			String msg = String.format(
					"Invalid status code encountered as "
							+ "part of API response. Response is: '%s'",
					statusCode);
			LOGGER.error(msg);
			throw new APIException(msg);
		}

		// All GSTN APIs return a status code as part of the response, which
		// can be directly read from the response string. A status code of 1
		// represents success, but the status code of 0 represents failure.
		return (statusCode.trim().equals("1") || statusCode.trim().equals("2")
				|| statusCode.trim().equals("3")) ? successHandler
						: failureHandler;
	}

}
