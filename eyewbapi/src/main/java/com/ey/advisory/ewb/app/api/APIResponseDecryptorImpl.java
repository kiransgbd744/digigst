package com.ey.advisory.ewb.app.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIProviderEnum;

@Component("EWBAPIResponseDecryptorImpl")
public class APIResponseDecryptorImpl implements APIResponseDecryptor {
	
	@Autowired
	@Qualifier("EWBAPIAuthTokenResponseDecryptor")
	APIResponseDecryptor apiAuthTokenResponseDecryptor;
	
	@Autowired
	@Qualifier("EWBAPINonAuthTokenResponseSKDecryptor")
	APIResponseDecryptor apiNonAuthTokenResponseSKDecryptor;
	
	@Autowired
	@Qualifier("EWBAPINonAuthTokenResponseRKDecryptor")
	APIResponseDecryptor apiNonAuthTokenResponseRKDecryptor;

	@Override
	public APIResponse decrypt(
			APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, String respJson) {
		String httpMethod = config.getConfigForVersion(version).getHttpMethod();
		if(params.apiProvider == APIProviderEnum.EWB) {
			if(params.getApiAction().equals(APIIdentifiers.GET_AUTH_TOKEN)) {
				return apiAuthTokenResponseDecryptor.decrypt(params,
					version, config, parties, context, respJson);
			} else if ("GET".equals(httpMethod)) {
				return apiNonAuthTokenResponseRKDecryptor.decrypt(params, 
						version, config, parties, context, respJson);
			}
			return apiNonAuthTokenResponseSKDecryptor.decrypt(params, 
				version, config, parties, context, respJson);
		} else {
			// Handle the GSTN API case here and remove the following
			// throw statement.
			throw new APIException(
					"GSTN API support is not included currently");
		}
	}
}
