package com.ey.advisory.ewb.app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.core.api.APIProviderEnum;

@Component("DefaultEWBNonStubExecutor")
public class DefaultEWBNonStubExecutor implements APIExecutor {

	@Autowired
	@Qualifier("EWBAPIExecutor")
	EWBAPIExecutor ewbAPIExecutor;

	@Override
	public APIResponse execute(APIParams params, String reqData) {
		APIResponse initialResponse = null;
		APIResponse authResponse = null;
		APIResponse updatedResponse = null;
		String id = params.getApiAction();
		try {
			initialResponse = ewbAPIExecutor.execute(params, reqData);
		} catch (AuthTokenExpiredException ex) {
			initialResponse = new APIResponse();
			APIError error = new APIError(APIErrorCodes.AUTH_TOKEN_UNAVAILABLE,
					"Auth Token Not Available in DB");
			initialResponse.addError(error);
		}

		if (initialResponse != null && initialResponse.isSuccess()) {
			return initialResponse;
		}

		if (APIIdentifiers.GET_AUTH_TOKEN.equals(id)) {
			return initialResponse;
		}

		APIError apiError = initialResponse.getErrors().get(0);
		String errorCode = apiError.getErrorCode();
		String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
		if (APIErrorCodes.INVALID_TOKEN.equals(errorCode)
				|| APIErrorCodes.INVALID_AUTH_TOKEN.equals(errorCode)
				|| APIErrorCodes.TOKEN_EXPIRED.equals(errorCode)
				|| APIErrorCodes.AUTH_TOKEN_UNAVAILABLE.equals(errorCode)) {
			KeyValuePair<String, String> keyValuePair = new KeyValuePair<>(
					"gstin", gstin);
			APIParams authParams = new APIParams(APIProviderEnum.EWB,
					APIIdentifiers.GET_AUTH_TOKEN, keyValuePair);
			authResponse = ewbAPIExecutor.execute(authParams, "");
			if (authResponse.isSuccess()) {
				updatedResponse = ewbAPIExecutor.execute(params, reqData);
				return updatedResponse;
			} else {
				return authResponse;
			}
		} else {
			return initialResponse;
		}

	}

}
