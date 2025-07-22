package com.ey.advisory.einv.app.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("EINVAPIResponseDecryptorImpl")
public class APIResponseDecryptorImpl implements APIResponseDecryptor {

	@Autowired
	@Qualifier("EINVAPIAuthTokenResponseDecryptor")
	APIResponseDecryptor apiAuthTokenResponseDecryptor;

	@Autowired
	@Qualifier("APINonAuthTokenResponseSKDecryptor")
	APIResponseDecryptor apiNonAuthTokenResponseSKDecryptor;

	@Autowired
	@Qualifier("APINonAuthTokenResponseRKDecryptor")
	APIResponseDecryptor apiNonAuthTokenResponseRKDecryptor;

	@Override
	public APIResponse decrypt(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, String respJson) {
		String httpMethod = config.getConfigForVersion(version).getHttpMethod();
		if (params.getApiAction().equals(APIIdentifiers.GET_EINV_AUTH_TOKEN)) {
			return apiAuthTokenResponseDecryptor.decrypt(params, version,
					config, parties, context, respJson);
		} else if ("GET".equals(httpMethod)) {
			return apiNonAuthTokenResponseRKDecryptor.decrypt(params, version,
					config, parties, context, respJson);
		}
		return apiNonAuthTokenResponseSKDecryptor.decrypt(params, version,
				config, parties, context, respJson);
	}
}
