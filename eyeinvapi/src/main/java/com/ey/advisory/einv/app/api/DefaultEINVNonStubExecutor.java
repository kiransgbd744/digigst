package com.ey.advisory.einv.app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.KeyValuePair;

@Component("DefaultEINVNonStubExecutor")
public class DefaultEINVNonStubExecutor implements APIExecutor {

	@Autowired
	@Qualifier("EINVAPIExecutor")
	APIExecutor einvAPIExecutor;

	@Override
	public APIResponse execute(APIParams params, String reqData) {
		APIResponse initialResponse = null;
		APIResponse authResponse = null;
		APIResponse updatedResponse = null;
		String id = params.getApiAction();
		try {
			initialResponse = einvAPIExecutor.execute(params, reqData);
		} catch (AuthTokenExpiredException ex) {
			initialResponse = new APIResponse();
			APIError error = new APIError(APIErrorCodes.AUTH_TOKEN_UNAVAILABLE,
					"Auth Token Not Available in DB");
			initialResponse.addError(error);
		}

		if (initialResponse != null && initialResponse.isSuccess()) {
			return initialResponse;
		}

		if (APIIdentifiers.GET_EINV_AUTH_TOKEN.equals(id)) {
			return initialResponse;
		}

		APIError apiError = initialResponse.getErrors().get(0);
		String errorCode = apiError.getErrorCode();
		String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
		if (APIErrorCodes.INVALID_TOKEN.equals(errorCode)
				|| APIErrorCodes.INVALID_TOKEN_VER2.equals(errorCode)
				|| APIErrorCodes.AUTH_TOKEN_UNAVAILABLE.equals(errorCode)) {
			KeyValuePair<String, String> keyValuePair = new KeyValuePair<>(
					APIReqParamConstants.GSTIN, gstin);
			APIParams authParams = new APIParams(
					CommonUtil.getApiProviderEnum(params),
					APIIdentifiers.GET_EINV_AUTH_TOKEN, keyValuePair);
			authResponse = einvAPIExecutor.execute(authParams, "");
			if (authResponse.isSuccess()) {
				updatedResponse = einvAPIExecutor.execute(params, reqData);
				return updatedResponse;
			} else {
				return authResponse;
			}
		} else {
			return initialResponse;
		}

	}

}
